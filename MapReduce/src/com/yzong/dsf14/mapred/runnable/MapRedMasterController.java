package com.yzong.dsf14.mapred.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.yzong.dsf14.mapred.dfs.FileProp;
import com.yzong.dsf14.mapred.dfs.ShardInfo;
import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ClusterStatus;
import com.yzong.dsf14.mapred.util.MapRedMessage;

/**
 * I/O Handler for MapRed Master server. Take in an object from <tt>ObjectInputStream</tt>, then
 * return a response to <tt>ObjectOutputStream</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMasterController implements Runnable {

  private ClusterConfig CC;
  private ClusterStatus CS;
  private ObjectInputStream InStream;
  private ObjectOutputStream OutStream;

  final static int TMP_NAME_LENGTH = 32;
  private Random random = new Random();

  public MapRedMasterController(ClusterConfig cc, ClusterStatus cs, ObjectInputStream in,
      ObjectOutputStream out) {
    CC = cc;
    CS = cs;
    InStream = in;
    OutStream = out;
  }

  /**
   * Counts the number of lines of a given file. (SO #453018)
   *
   * @param fileName File name for the file to count.
   * @return Number of lines in the file.
   * @throws IOException When the path is invalid, or other I/O exceptions occur.
   */
  private int countLines(String fileName) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(fileName));
    try {
      byte[] c = new byte[1024];
      int count = 0;
      int readChars = 0;
      boolean empty = true;
      while ((readChars = is.read(c)) != -1) {
        empty = false;
        for (int i = 0; i < readChars; ++i) {
          if (c[i] == '\n') {
            ++count;
          }
        }
      }
      return (count == 0 && !empty) ? 1 : count;
    } finally {
      is.close();
    }
  }

  /**
   * Handles a <i>Get File</i> request from client.
   * 
   * @param inPkg Client request package.
   * @return Response from Master Node.
   */
  MapRedMessage DfsGet(MapRedMessage inPkg) {
    String fileName = (String) (((Object[]) inPkg.Body)[0]);
    String localPath = (String) (((Object[]) inPkg.Body)[1]);
    if (!CS.getDfs().FileList.containsKey(fileName)) {
      System.out.printf("Error: attempted to obtain inexistent file `%s`!\n", fileName);
      return null;
    }
    System.out.printf("Polling shards of %s from worker nodes...\n", fileName);
    FileProp f = CS.getDfs().FileList.get(fileName);
    int shards = f.NumShards;
    /* Find each chunk of the file from workers. */
    for (int i = 0; i < shards; i++) {
      MapRedMessage outPkg = null;
      boolean succeed = false;
      for (String worker : CC.getDfs().Wkrs.keySet()) {
        int idx = -1;
        if ((idx = CS.getDfs().LookupTable.get(worker).indexOf(new ShardInfo(fileName, i, ""))) != -1) {
          outPkg =
              new MapRedMessage("DFS/GET",
                  CS.getDfs().LookupTable.get(worker).get(idx).RemotePath);
          try {
            MapRedMessage response = DfsSendRequest(worker, outPkg);
            if (!response.Command.equals("OK")) {
              System.out.printf("Error while loading file %s:%d from %s: %s!\n", fileName, i,
                  worker, (String) response.Body);
            } else {
              String text = (String) response.Body;
              String outPath = CS.getDfs().WorkingDir + "/" + fileName + "." + i;
              BufferedWriter outBuffer = new BufferedWriter(new FileWriter(outPath));
              outBuffer.write(text);
              outBuffer.flush();
              outBuffer.close();
              succeed = true;
              break; // Move on to next shard!
            }
          }
          /* Connection error while grabbing a shard -- move on. */
          catch (Exception e) {
            System.out.printf("Warning: I/O exception while loading file %s:%d from %s -- %s\n",
                fileName, i, worker, e.getMessage());
          }
        }
      }
      /* If the shard is not found anywhere, raise an error! */
      if (!succeed) {
        System.out.printf("Shard `%s:%d` not found on any worker node!\n", fileName, i);
        CS.getDfs().FileList.remove(fileName); // Remove the file from "available DFS file" list.
        for (int j = 0; j < i; j++) {
          FileUtils.deleteQuietly(new File(CS.getDfs().WorkingDir + "/" + fileName + "." + i));
        }
        return null;
      }
    }
    /* All `GET`s have succeeded. Stick files together. (SO #10675450) */
    System.out.printf("Merging shards of %s into target file...\n", fileName);
    List<Path> inputs = new ArrayList<Path>();
    for (int i = 0; i < shards; i++) {
      inputs.add(Paths.get(CS.getDfs().WorkingDir + "/" + fileName + "." + i));
    }
    Path output = Paths.get(localPath);
    Charset charset = StandardCharsets.UTF_8;
    try {
      // Remove the file if already exists.
      if (new File(localPath).exists()) {
        new File(localPath).delete();
      }
      for (Path path : inputs) {
        List<String> lines = Files.readAllLines(path, charset);
        Files.write(output, lines, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      }
      System.out.printf("Succeed outputing DFS `%s` to local `%s`!\n", fileName, localPath);
      return new MapRedMessage("OK", null);
    }
    /* Exception occured while merging to output file... */
    catch (Exception e) {
      System.out.printf("Error while merging the shards: %s\n", e.getMessage());
      return new MapRedMessage("XXX", e.getMessage());
    }
  }

  /**
   * Handles a <i>Load File</i> request from client.
   * 
   * @param inPkg Client request package.
   * @return Response from Master Node.
   */
  MapRedMessage DfsLoad(MapRedMessage inPkg) {
    /* First load the byte stream to a tmp file. */
    String fileName = (String) (((Object[]) inPkg.Body)[0]);
    byte[] fileContent = (byte[]) (((Object[]) inPkg.Body)[1]);
    String tmpFileBuffer = CS.getDfs().WorkingDir + "/" + RandomStringUtils.randomAlphanumeric(32);
    try {
      FileUtils.writeByteArrayToFile(new File(tmpFileBuffer), fileContent);
    } catch (IOException e) {
      System.err.println("Unable to access local working directory...");
      return null;
    }
    final int lineLimit = CC.getDfs().ShardSize;
    if (CS.getDfs().FileList.containsKey(fileName)) {
      System.out.printf("Error -- File `%s` already exists!", fileName);
      FileUtils.deleteQuietly(new File(tmpFileBuffer));
      return null;
    }
    try {
      int lineNum = countLines(tmpFileBuffer);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(tmpFileBuffer));
      StringBuffer strBuffer = new StringBuffer();
      int fileCounter = 0;
      int lineCounter = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        lineCounter++;
        strBuffer.append(line);
        strBuffer.append(System.getProperty("line.separator"));
        if (lineCounter >= lineLimit) {
          DfsPushFile(strBuffer, fileName, fileCounter);
          strBuffer = new StringBuffer();
          lineCounter = 0;
          fileCounter++;
        }
      }
      if (lineCounter > 0) {
        DfsPushFile(strBuffer, fileName, fileCounter);
        fileCounter++;
      }
      bufferedReader.close();
      /* Upon success, add the file to official file list. */
      CS.getDfs().FileList.put(fileName, new FileProp(fileName, lineNum, fileCounter));
      FileUtils.deleteQuietly(new File(tmpFileBuffer));
      return new MapRedMessage("OK", null);
    } catch (Exception e) {
      FileUtils.deleteQuietly(new File(tmpFileBuffer));
      return new MapRedMessage("XXX", e.getMessage());
    }
  }

  /**
   * Helper function that loads a chunk of file onto a pre-defined number of nodes.
   *
   * @param buffer StringBuffer containing the file content.
   * @param fileName FileName for DFS.
   * @param fileCounter Chunk counter for the current chunk.
   * @return <tt>true</tt> iff the opreation succeeded.
   */
  private boolean DfsPushFile(StringBuffer buffer, String fileName, int fileCounter) {
    /* Prepare package and a list of random recipients. */
    MapRedMessage outPkg =
        new MapRedMessage("DFS/ADD", new Object[] {buffer, fileName + "." + fileCounter});
    List<String> targets = new ArrayList<String>();
    List<String> workers = new ArrayList<String>(CC.getDfs().Wkrs.keySet());
    for (int i = 0; i < Math.min(CC.getDfs().Replication, workers.size()); i++) {
      int newIdx = random.nextInt(workers.size());
      while (targets.contains(workers.get(newIdx))) {
        newIdx = random.nextInt(workers.size());
      }
      targets.add(workers.get(newIdx));
    }
    /* Starting pushing shards to worker nodes. */
    for (String w : targets) {
      try {
        MapRedMessage response = DfsSendRequest(w, outPkg);
        if (!response.Command.equals("OK")) {
          System.out.printf("Error while loading file: %s!\n", (String) response.Body);
        } else {
          System.out.printf("Pushed %s:%d to worker `%s`.\n", fileName, fileCounter, w);
          /* Upon success, log it into DFS Lookup Table. */
          List<ShardInfo> currentShards = CS.getDfs().LookupTable.get(w);
          currentShards.add(new ShardInfo(fileName, fileCounter, (String) response.Body));
          CS.getDfs().LookupTable.put(w, currentShards);
        }
      }
      /* If connection error occurs, ignore and continue. */
      catch (Exception e) {
        System.out.printf("Warning: Worker %s:%d not reachable!\n",
            CC.getDfs().Wkrs.get(w).HostName, CC.getDfs().Wkrs.get(w).PortNum);
      }
    }
    return true;
  }

  /**
   * Sends a DfsCommunicationPkg to a certain worker.
   * 
   * @param w Name of worker node.
   * @param outPkg Package to send.
   * @return the response package from worker
   * @throws IOException
   * @throws UnknownHostException
   * @throws ClassNotFoundException
   */
  private MapRedMessage DfsSendRequest(String w, MapRedMessage outPkg)
      throws UnknownHostException, IOException, ClassNotFoundException {
    Socket outSocket =
        new Socket(CC.getDfs().Wkrs.get(w).HostName, CC.getDfs().Wkrs.get(w).PortNum);
    ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
    ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
    out.writeObject(outPkg);
    MapRedMessage response = (MapRedMessage) in.readObject();
    outSocket.close();
    return response;
  }


  /**
   * Cleans up and destroys the current MapReduce cluster.
   * 
   * @return Response from Master node.
   */
  private MapRedMessage AllDestroy() {
    String message = "";
    try {
      /* Send `destroy` message to each worker. */
      for (String w : CC.getDfs().Wkrs.keySet()) {
        Socket outSocket =
            new Socket(CC.getDfs().Wkrs.get(w).HostName, CC.getDfs().Wkrs.get(w).PortNum);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(new MapRedMessage("DESTROY", null));
        String msg = ((MapRedMessage) in.readObject()).Command;
        if (!msg.equals("OK")) {
          message +=
              String.format("Error occured while cleaning up %s. Please do so manually.\n", w);
        }
        outSocket.close();
      }
      /* Clean up Master's own working directory */
      FileUtils.deleteDirectory(new File(CS.getDfs().WorkingDir));
      if (message.equals("")) {
        return new MapRedMessage("OK", null);
      } else {
        return new MapRedMessage("XXX", message);
      }
    } catch (Exception e) {
      System.out.printf("Error while destroying cluster -- %s\n", e.getMessage());
      return new MapRedMessage("XXX", e.getMessage());
    }
  }

  /**
   * Dispatches client request packages to dedicated functions, and write the response.
   */
  @Override
  synchronized public void run() {
    try {
      MapRedMessage inPkg = (MapRedMessage) InStream.readObject();
      String inCommand = inPkg.Command;
      MapRedMessage outPkg = null;

      /* Case One: Client wishes to load file onto DFS. */
      if (inCommand.equals("DFS/LOAD")) {
        outPkg = DfsLoad(inPkg);
      }
      /* Case Two: Client wishes to pull file from DFS. */
      else if (inCommand.equals("DFS/PULL")) {
        outPkg = DfsGet(inPkg);
      }
      /* Case Three: Client wishes to destroy cluster. */
      else if (inCommand.equals("ALL/DESTROY")) {
        outPkg = AllDestroy();
        Thread.currentThread().interrupt();
      } else {
        outPkg = new MapRedMessage("XXX", String.format("Command %s not recognized!", inCommand));
      }
      /* Feed the output back to the OutputObjectStream. */
      OutStream.writeObject(outPkg);
    }
    /* Catches any exception, logs to stdio, and continue. */
    catch (Exception e) {
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
  }

}

package com.yzong.dsf14.mapred.dfs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

/**
 * Main controller that keeps track of and manipulates files on the DFS.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsController {

  public DfsCluster ClusterConfig;
  public List<FileProp> FileList;
  public HashMap<String, List<ShardInfo>> LookupTable;
  public String SessionID;
  public String DirPath;

  private Random random = new Random();

  public DfsController(DfsCluster cluster) {
    this.SessionID = RandomStringUtils.randomAlphanumeric(8);
    System.out.printf("Initializing session `%s`...\n", SessionID);
    this.ClusterConfig = cluster; // Save cluster info
    this.FileList = new ArrayList<FileProp>(); // Empty list of files
    this.LookupTable = new HashMap<String, List<ShardInfo>>(); // Worker Node -> File Shards
    // Initialize the shard list of each node as empty.
    for (String name : cluster.WorkerConfig.keySet()) {
      this.LookupTable.put(name, new ArrayList<ShardInfo>());
    }
    /* Create a `tmp` directory for holding temp files */
    new File("./tmp-DFS-" + SessionID).mkdir();
    this.DirPath = "./tmp-DFS-" + SessionID;
  }

  /**
   * Counts the number of lines of a given file. (StackOverflow #453018)
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
   * Loads a file in the local file system onto DFS.
   * 
   * @param localPath Path of the file to be loaded from local file system.
   * @param fileName Filename of the file in DFS.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean putFile(String localPath, String fileName) {
    final int lineLimit = ClusterConfig.ShardSize;
    if (FileList.contains(fileName)) {
      System.out.printf("File `%s` already exists!", fileName);
      return false;
    }
    try {
      int lineNum = countLines(localPath);
      BufferedReader bufferedReader = new BufferedReader(new FileReader(localPath));
      StringBuffer strBuffer = new StringBuffer();
      int fileCounter = 0;
      int lineCounter = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        lineCounter++;
        strBuffer.append(line);
        strBuffer.append(System.getProperty("line.separator"));
        if (lineCounter >= lineLimit) {
          pushFile(strBuffer, fileName, fileCounter);
          strBuffer = new StringBuffer();
          lineCounter = 0;
          fileCounter++;
        }
      }
      if (lineCounter > 0) {
        pushFile(strBuffer, fileName, fileCounter);
        fileCounter++;
      }
      bufferedReader.close();
      /* Upon success, add the file to official file list. */
      FileList.add(new FileProp(fileName, lineNum, fileCounter));
      return true;
    } catch (Exception e) {
      System.out.printf("Error while processing %s->%s!\n", localPath, fileName);
      return false;
    }
  }

  /**
   * Helper function that loads a chunk of file onto a pre-defined number of nodes.
   *
   * @param buffer StringBuffer containing the file content.
   * @param fileName FileName for DFS.
   * @param fileCounter Chunk counter for the current chunk.
   * @return
   */
  private boolean pushFile(StringBuffer buffer, String fileName, int fileCounter) {
    /* Prepare package and a list of random recipients. */
    DfsCommunicationPkg outPkg =
        new DfsCommunicationPkg("ADD", new Object[] {buffer, fileName + "." + fileCounter});
    List<String> targets = new ArrayList<String>();
    List<String> workers = new ArrayList<String>(ClusterConfig.WorkerConfig.keySet());
    for (int i = 0; i < Math.min(ClusterConfig.Replication, workers.size()); i++) {
      int newIdx = random.nextInt(workers.size());
      while (targets.contains(workers.get(newIdx))) {
        newIdx = random.nextInt(workers.size());
      }
      targets.add(workers.get(newIdx));
    }
    for (String w : targets) {
      try {
        Socket outSocket =
            new Socket(ClusterConfig.WorkerConfig.get(w).HostName,
                ClusterConfig.WorkerConfig.get(w).PortNum);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(outPkg);
        DfsCommunicationPkg response = (DfsCommunicationPkg) in.readObject();
        if (!response.Command.equals("OK")) {
          System.out.printf("Error while loading file: %s!\n", (String) response.Body);
          outSocket.close();
          return false;
        } else {
          System.out.printf("Pushed %s:%d to worker `%s`.\n", fileName, fileCounter, w);
          /* Upon success, log it into DFS Lookup Table. */
          List<ShardInfo> currentShards = LookupTable.get(w);
          currentShards.add(new ShardInfo(fileName, fileCounter, (String) response.Body));
          LookupTable.put(w, currentShards);
        }
        outSocket.close();
      }
      /* If connection error occurs, ignore and continue. */
      catch (Exception e) {
        System.out.printf("Warning: Worker %s:%d not reachable! Will retry in 2 seconds...\n",
            ClusterConfig.WorkerConfig.get(w).HostName, ClusterConfig.WorkerConfig.get(w).PortNum);
      }
    }
    return true;
  }

  /**
   * Obtains a local copy of a file in DFS.
   * 
   * @param fileName Filename of the file in DFS.
   * @param localPath Target path to store the file in local file system.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean getFile(String fileName, String localPath) {
    if (!FileList.contains(fileName)) {
      System.out.printf("Error: attempted to obtain inexistent file `%s`!\n", fileName);
      return false;
    }

    return false;
  }

  /**
   * Waits for all worker nodes in DFS to be ready.
   * 
   * @return <tt>true</tt> iff the cluster initialized successfully.
   */
  public boolean waitForDFS() {
    List<String> unavailableWorker = new ArrayList<String>(ClusterConfig.WorkerConfig.keySet());
    while (unavailableWorker.size() != 0) {
      System.out.printf("\nWaiting for worker nodes... (%d remaining)\n", unavailableWorker.size());
      for (Iterator<String> i = unavailableWorker.iterator(); i.hasNext();) {
        String w = i.next();
        Socket outSocket;
        try {
          outSocket =
              new Socket(ClusterConfig.WorkerConfig.get(w).HostName,
                  ClusterConfig.WorkerConfig.get(w).PortNum);
          ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
          out.writeObject(new DfsCommunicationPkg("PING", null));
          String response = ((DfsCommunicationPkg) in.readObject()).Command;
          outSocket.close();
          if (response.equals("PONG")) {
            i.remove();
          }
        }
        /* If connection error occurs, ignore and continue. */
        catch (Exception e) {
          System.out
              .printf("Warning: Worker %s:%d not reachable! Will retry in 2 seconds...\n",
                  ClusterConfig.WorkerConfig.get(w).HostName,
                  ClusterConfig.WorkerConfig.get(w).PortNum);
        }
      }
      try {
        Thread.sleep(2000); // Waits for 2 seconds before another round.
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
    System.out.println("\nCluster initialized successfully.");
    return true;
  }
}

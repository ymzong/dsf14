package com.yzong.dsf14.mapred.dfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.FileUtils;

import com.yzong.dsf14.mapred.util.ConfigManager;

/**
 * Entry point for the distributed file system Worker instance. It handles Read/Write requests from
 * the Master node.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class DfsWorker {
  public final static String CFG_PATH = "conf/cluster.xml";
  public static int LocalPort;

  /**
   * Entry point for Worker instance.
   * 
   * @param args Port number to listen.
   */
  public static void main(String[] args) {
    /* Obtain local port number from argument. */
    try {
      LocalPort = Integer.parseInt(args[0]);
    } catch (Exception e) {
      System.err.println("Please input a valid port number as the first argument!");
      System.exit(1);
    }
    /* Load the cluster topology. */
    ConfigManager cm = new ConfigManager(CFG_PATH);
    DfsConfig clusterStatus = cm.parseDFSConfig();
    if (clusterStatus == null) {
      System.out.println("Please fix your config file and try again!");
      System.exit(1);
    }
    DfsController DC = new DfsController(clusterStatus);
    System.out.printf("Config file loaded from %s!\n", CFG_PATH);
    /* Spin up DFS Worker Server to handle requests. */
    ServerSocket srv = null;
    try {
      srv = new ServerSocket(LocalPort);
      System.out.printf("INFO -- DFS Worker started at localhost:%d...\n", LocalPort);
      /* Mainloop that accepts requests R/W requests. */
      while (true) {
        try {
          Socket workerSocket = srv.accept();
          ObjectInputStream in = new ObjectInputStream(workerSocket.getInputStream());
          ObjectOutputStream out = new ObjectOutputStream(workerSocket.getOutputStream());
          DfsCommunicationPkg inPkg = (DfsCommunicationPkg) in.readObject();
          /* Case One: Heartbeat request. */
          if (inPkg.Command.equals("PING")) {
            System.out.println("INFO -- `PING` request received from client.");
            out.writeObject(new DfsCommunicationPkg("PONG", null));
          }
          /* Case Two: Master pushes shard request. */
          else if (inPkg.Command.equals("ADD")) {
            System.out.println("INFO -- `ADD` request received from client.");
            DfsCommunicationPkg outPkg = null;
            try {
              Object[] bodyArgs = (Object[]) inPkg.Body;
              StringBuffer text = (StringBuffer) bodyArgs[0];
              String fileName = (String) bodyArgs[1];
              String outPath = DC.DirPath + "/" + fileName;
              BufferedWriter outBuffer = new BufferedWriter(new FileWriter(outPath));
              outBuffer.write(text.toString());
              outBuffer.flush();
              outBuffer.close();
              outPkg = new DfsCommunicationPkg("OK", outPath);
            } catch (Exception e) {
              outPkg = new DfsCommunicationPkg("XXX", e.getMessage());
              System.out.printf("ERROR -- %s happened while processing `ADD`.\n", e.getMessage());
            }
            out.writeObject(outPkg);
          }
          /* Case Three: Client requests file shard. */
          else if (inPkg.Command.equals("GET")) {
            System.out.println("INFO -- `GET` request received from client.");
            DfsCommunicationPkg outPkg = null;
            try {
              String localPath = (String) inPkg.Body;
              outPkg = new DfsCommunicationPkg("OK", FileUtils.readFileToString(new File(localPath)));
            } catch (Exception e) {
              outPkg = new DfsCommunicationPkg("XXX", e.getMessage());
              System.out.printf("ERROR -- %s happened while processing `GET`.\n", e.getMessage());
            }
            out.writeObject(outPkg);
          }
          /* Case Four: Master terminates DFS sessoin. */
          else if (inPkg.Command.equals("DESTROY")) {
            /* Remove local tmp directory */
            System.out.println("INFO -- `DESTROY` request received from Master.");
            out.writeObject(new DfsCommunicationPkg("OK", null));
            System.out.println("INFO -- Cleaning up working directory...");
            FileUtils.deleteDirectory(new File(DC.DirPath));
            return;
          }
          /* Case Five: Incoming package cannot be interpreted. */
          else {
            System.out.println("ERROR -- Incoming package cannot be interpreted!");
            out.writeObject(new DfsCommunicationPkg("XXX", null));
          }
        }
        /* Simply display the error so as not to interrupt service. */
        catch (Exception e) {
          System.out.printf("ERROR -- %s\n", e.getMessage());
        }
      }
    }
    /* Gracefully exit in case of exception */
    catch (Exception e) {
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
    /* Clean up server socket in any case */
    finally {
      try {
        srv.close();
      } catch (IOException e) {
      }
      System.out.println("INFO -- Goodbye!");
    }
  }

}

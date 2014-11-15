package com.yzong.dsf14.mapred.dfs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
    cm.parseDFSConfig();
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
            System.out.printf("INFO -- `PING` request received from client.");
            out.writeObject(new DfsCommunicationPkg("PONG", null));
          }
          /* Case Two: Master pushes shard request. */
          else if (inPkg.Command.equals("ADD")) {

          }
          /* Case Three: Client requests file shard. */
          else if (inPkg.Command.equals("GET")) {

          }
          /* Case Four: Incoming package cannot be interpreted. */
          else {
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

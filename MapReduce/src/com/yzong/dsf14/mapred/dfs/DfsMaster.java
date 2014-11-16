package com.yzong.dsf14.mapred.dfs;

import java.io.Console;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.io.FileUtils;

import com.yzong.dsf14.mapred.util.ConfigManager;


/**
 * Entry point for the distributed file system Master CLI. It supports Read/Write operations issued
 * by the user on the command line.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class DfsMaster {

  public final static String CFG_PATH = "conf/cluster.xml";

  /**
   * Entry point for DFS Master CLI.
   * 
   * @param args Not used.
   */
  public static void main(String[] args) {
    /* Load parameters from Config File. Verify participants. */
    ConfigManager cm = new ConfigManager(CFG_PATH);
    DfsCluster clusterStatus = cm.parseDFSConfig();
    if (clusterStatus == null) {
      System.out.println("Please fix your config file and try again!");
      System.exit(1);
    }
    System.out.printf("Config file loaded from %s!\n", CFG_PATH);
    DfsController DC = new DfsController(clusterStatus);
    DC.waitForDFS();
    /* Start the DFS Master Shell. */
    Console console = System.console();
    String input = console.readLine("DFS Master > ");
    while (!input.equals("quit") && !input.equals("exit")) {
      // TODO: mainloop.
      DC.putFile("syslog", "coolstuff");
      input = console.readLine("\nDFS Master > ");
      DC.getFile("coolstuff", "resultingfile");
      input = console.readLine("\nBlah");
    }
    /* When user exists, clean up the entire file system. */
    System.out.println("Cleaning up DFS...");
    try {
      /* Send `destroy` message to each worker. */
      for (String w : clusterStatus.WorkerConfig.keySet()) {
        Socket outSocket =
            new Socket(clusterStatus.WorkerConfig.get(w).HostName,
                clusterStatus.WorkerConfig.get(w).PortNum);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(new DfsCommunicationPkg("DESTROY", null));
        String msg = ((DfsCommunicationPkg) in.readObject()).Command;
        if (!msg.equals("OK")) {
          System.out.printf("Error occured while cleaning up %s. Please do so manually.\n", w);
        }
        outSocket.close();
      }
      /* Clean up Master's own working directory */
      FileUtils.deleteDirectory(new File(DC.DirPath));
    } catch (Exception e) {
      /* Ignore any exceptions occured during clean-up. */
    } finally {
      System.out.println("Done. Goodbye!");
      System.exit(0);
    }
  }
}

package com.yzong.dsf14.mapred.dfs;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
   * Loads a file in the local file system onto DFS.
   * 
   * @param localPath Path of the file to be loaded in local file system.
   * @param fileName Filename of the file in DFS.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean putFile(String localPath, String fileName) {
    // TODO: Complete `putFile`.
    return false;
  }

  /**
   * Obtains a local copy of a file in DFS.
   * 
   * @param fileName Filename of the file in DFS.
   * @param localPath Target path to store the file in local file system.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean getFile(String fileName, String localPath) {
    // TODO: Complete `getFile`.
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

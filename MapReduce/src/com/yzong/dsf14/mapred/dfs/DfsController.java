package com.yzong.dsf14.mapred.dfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Main controller that keeps track of and manipulates files on the DFS.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsController {

  public DFSCluster ClusterConfig;
  public List<FileProp> FileList;
  public HashMap<String, List<ShardInfo>> LookupTable;

  public DfsController(DFSCluster cluster) {
    this.ClusterConfig = cluster; // Save cluster info
    this.FileList = new ArrayList<FileProp>(); // Empty list of files
    this.LookupTable = new HashMap<String, List<ShardInfo>>(); // Worker Node -> File Shards
    // Initialize the shard list of each node as empty.
    for (String name : cluster.WorkerConfig.keySet()) {
      this.LookupTable.put(name, new ArrayList<ShardInfo>());
    }
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
    Set<String> unavailableWorker = ClusterConfig.WorkerConfig.keySet();
    int remainingRound = 10;
    while (unavailableWorker.size() != 0 && remainingRound > 0) {
      remainingRound--;
      System.out.printf("Waiting for worker nodes... %d remaining...\n", unavailableWorker.size());
      // TODO: Contact work nodes.
      try {
        Thread.sleep(2000); // Waits for 2 seconds before another round.
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
    if (unavailableWorker.size() == 0) {
      System.out.println("Cluster initialized successfully.");
      return true;
    } else {
      System.out.printf("Cannot establish connection with %d nodes!\n", unavailableWorker.size());
      return false;
    }
  }
}

package com.yzong.dsf14.mapred.dfs;

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
    DfsCluster dfsCluster =
        new DfsCluster(cm.DFSClusterStatus.MasterHost, cm.DFSClusterStatus.MasterPort,
            cm.DFSClusterStatus.ShardSize, cm.DFSClusterStatus.Replication,
            cm.DFSClusterStatus.WorkerConfig);
    DfsController DC = new DfsController(dfsCluster);
    DC.waitForDFS();
    
  }

}

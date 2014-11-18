package com.yzong.dsf14.mapred.dfs;

import java.util.HashMap;

/**
 * 
 * Encapsulates all configuration parameters of the Distributed File System for MapReduce cluster,
 * i.e. <tt>MasterHost</tt>, <tt>MasterPortNumber</tt>, <tt>ShardSize</tt>, <tt>Replication</tt>,
 * and <tt>WorkerName</tt> --> <tt>DFSWorkerConfig</tt> hashmap.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsConfig {

  public String MasterHost;
  public int MasterPort;
  public int ShardSize;
  public int Replication;
  public HashMap<String, DfsWorkerConfig> Wkrs;

  public DfsConfig(String masterHost, int masterPort, int shardSize, int replication,
      HashMap<String, DfsWorkerConfig> workerConfig) {
    MasterHost = masterHost;
    MasterPort = masterPort;
    ShardSize = shardSize;
    Replication = replication;
    Wkrs = workerConfig;
  }

}

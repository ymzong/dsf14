/**
 * 
 */
package com.yzong.dsf14.mapred.dfs;

import java.util.HashMap;

/**
 * 
 * Encapsulates all configuration parameters of the Distributed File System and MapReduce cluster,
 * i.e. <tt>MasterHost</tt>, <tt>MasterPortNumber</tt>, <tt>ShardSize</tt>, <tt>Replication</tt>, and <tt>Worker</tt>-to-<tt>DFSWorker</tt>
 * hashmap.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DFSCluster {

  public String MasterHost;
  public int MasterPort;
  public int ShardSize;
  public int Replication;
  public HashMap<String, DFSWorkerInfo> WorkerConfig;

  public DFSCluster(String masterHost, int masterPort, int shardSize, int replication,
      HashMap<String, DFSWorkerInfo> workerConfig) {
    MasterHost = masterHost;
    MasterPort = masterPort;
    ShardSize = shardSize;
    Replication = replication;
    WorkerConfig = workerConfig;
  }

}

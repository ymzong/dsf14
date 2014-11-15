package com.yzong.dsf14.mapred.mapred;

import java.util.HashMap;

/**
 * 
 * Encapsulates all configuration parameters of the Distributed File System and MapReduce cluster,
 * i.e. <tt>MasterHost</tt>, <tt>MasterPortNumber</tt>, and <tt>Worker</tt>-to-<tt>DFSWorker</tt>
 * hashmap.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedCluster {

  public String MasterHost;
  public int MasterPort;
  public HashMap<String, MRWorkerInfo> WorkerConfig;

  public MapRedCluster(String masterHost, int masterPort, HashMap<String, MRWorkerInfo> workerConfig) {
    MasterHost = masterHost;
    MasterPort = masterPort;
    WorkerConfig = workerConfig;
  }

}

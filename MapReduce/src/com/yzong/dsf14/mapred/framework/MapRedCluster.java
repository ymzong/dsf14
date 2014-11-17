package com.yzong.dsf14.mapred.framework;

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
  public HashMap<String, MapRedWorkerInfo> WorkerConfig;

  public MapRedCluster(String masterHost, int masterPort, HashMap<String, MapRedWorkerInfo> workerConfig) {
    MasterHost = masterHost;
    MasterPort = masterPort;
    WorkerConfig = workerConfig;
  }

}

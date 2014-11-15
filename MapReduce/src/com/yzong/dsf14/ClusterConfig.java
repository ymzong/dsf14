/**
 * 
 */
package com.yzong.dsf14;

import java.util.HashMap;

/**
 * 
 * Encapsulates all configuration parameters of the Distributed File System and MapReduce cluster.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ClusterConfig {

  public String MasterHost;
  public int MasterPort;
  public HashMap<String, WorkerInfo> WorkerConfig;

  public ClusterConfig(String masterHost, int masterPort, HashMap<String, WorkerInfo> workerConfig) {
    MasterHost = masterHost;
    MasterPort = masterPort;
    WorkerConfig = workerConfig;
  }

}

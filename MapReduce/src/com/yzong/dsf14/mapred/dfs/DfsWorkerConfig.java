package com.yzong.dsf14.mapred.dfs;

/**
 * 
 * Encapsulates all configuration parameters of a node in the DFS cluster, i.e. <tt>HostName</tt>
 * and <tt>PortNumber</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsWorkerConfig {
  public String HostName;
  public int PortNum;

  public DfsWorkerConfig(String hostName, int portNum) {
    HostName = hostName;
    PortNum = portNum;
  }

}

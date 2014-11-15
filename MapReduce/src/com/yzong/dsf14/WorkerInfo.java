/**
 * 
 */
package com.yzong.dsf14;

/**
 * 
 * Encapsulates all configuration parameters of a node in the cluster.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class WorkerInfo {
  public String HostName;
  public int PortNum;
  public int MaxMapper;
  public int MaxReducer;

  public WorkerInfo(String hostName, int portNum, int maxMapper, int maxReducer) {
    HostName = hostName;
    PortNum = portNum;
    MaxMapper = maxMapper;
    MaxReducer = maxReducer;
  }

}

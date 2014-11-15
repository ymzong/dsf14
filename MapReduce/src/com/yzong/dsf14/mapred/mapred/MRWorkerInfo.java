package com.yzong.dsf14.mapred.mapred;

/**
 * 
 * Encapsulates all configuration parameters of a node in the <i>MapReduce</i> cluster, i.e.
 * <tt>HostName</tt>, <tt>PortNumber</tt>, <tt>MaxMapper</tt>, and <tt>MaxReducer</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MRWorkerInfo {
  public String HostName;
  public int PortNum;
  public int MaxMapper;
  public int MaxReducer;

  public MRWorkerInfo(String hostName, int portNum, int maxMapper, int maxReducer) {
    HostName = hostName;
    PortNum = portNum;
    MaxMapper = maxMapper;
    MaxReducer = maxReducer;
  }
}

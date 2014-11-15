/**
 * 
 */
package com.yzong.dsf14.mapred.dfs;

/**
 * 
 * Encapsulates all configuration parameters of a node in the <i>File System</i> cluster, i.e.
 * <tt>HostName</tt> and <tt>PortNumber</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DFSWorkerInfo {
  public String HostName;
  public int PortNum;

  public DFSWorkerInfo(String hostName, int portNum) {
    HostName = hostName;
    PortNum = portNum;
  }

}

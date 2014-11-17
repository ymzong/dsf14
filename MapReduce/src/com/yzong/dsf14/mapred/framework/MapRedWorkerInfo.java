package com.yzong.dsf14.mapred.framework;

/**
 * 
 * Encapsulates all configuration parameters of a node in the <i>MapReduce</i> cluster, i.e.
 * <tt>HostName</tt>, <tt>PortNumber</tt>, <tt>MaxMapper</tt>, and <tt>MaxReducer</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedWorkerInfo {
  public String HostName;
  public int PortNum;
  public int MaxMapper;
  public int MaxReducer;

  public MapRedWorkerInfo(String hostName, int portNum, int maxMapper, int maxReducer) {
    HostName = hostName;
    PortNum = portNum;
    MaxMapper = maxMapper;
    MaxReducer = maxReducer;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((HostName == null) ? 0 : HostName.hashCode());
    result = prime * result + PortNum;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MapRedWorkerInfo other = (MapRedWorkerInfo) obj;
    if (HostName == null) {
      if (other.HostName != null)
        return false;
    } else if (!HostName.equals(other.HostName))
      return false;
    if (PortNum != other.PortNum)
      return false;
    return true;
  }
  
  
}

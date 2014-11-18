package com.yzong.dsf14.mapred.util;

import com.yzong.dsf14.mapred.dfs.DfsStatus;
import com.yzong.dsf14.mapred.framework.MapRedStatus;

/**
 * A wrapper object for DFS and MapReduce status objects. It includes all *soft* states of the
 * instances, like DFS LookupTable, task list, etc.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ClusterStatus {

  private DfsStatus dfs;
  private MapRedStatus mr;

  public ClusterStatus(DfsStatus DC, MapRedStatus MC) {
    dfs = DC;
    mr = MC;
  }

  /**
   * @return the DFS Config
   */
  public DfsStatus getDfs() {
    return dfs;
  }

  /**
   * @param dfs the DFSConfig to set
   */
  public void setDfs(DfsStatus dfs) {
    this.dfs = dfs;
  }

  /**
   * @return the MapRed Config
   */
  public MapRedStatus getMr() {
    return mr;
  }

  /**
   * @param mr the MapRedConfig to set
   */
  public void setMr(MapRedStatus mr) {
    this.mr = mr;
  }

}

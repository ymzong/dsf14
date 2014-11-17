package com.yzong.dsf14.mapred.util;

import com.yzong.dsf14.mapred.dfs.DfsConfig;
import com.yzong.dsf14.mapred.framework.MapRedConfig;

/**
 * A wrapper object for DFS- and MapReduce-status objects.
 *  
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ClusterConfig {
  
  private DfsConfig dfs;
  private MapRedConfig mr;
  
  public ClusterConfig(DfsConfig DC, MapRedConfig MC) {
    dfs = DC;
    mr = MC;
  }

  /**
   * @return the DFS Config
   */
  public DfsConfig getDfs() {
    return dfs;
  }

  /**
   * @param dfs the DFSConfig to set
   */
  public void setDfs(DfsConfig dfs) {
    this.dfs = dfs;
  }

  /**
   * @return the MapRed Config
   */
  public MapRedConfig getMr() {
    return mr;
  }

  /**
   * @param mr the MapRedConfig to set
   */
  public void setMr(MapRedConfig mr) {
    this.mr = mr;
  }
  
}

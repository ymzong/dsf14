package com.yzong.dsf14.mapred.dfs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;

import com.yzong.dsf14.mapred.util.ClusterConfig;

/**
 * Encapsulates all status information of the Distributed File System, including <tt>FileList</tt>,
 * <tt>LookupTable</tt>, <tt>SessionID</tt>, and <tt>WorkingDirectory</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsStatus {

  /**
   * Unique sesion ID of DFS Master instance.
   */
  public String SessionID;
  /**
   * Unique working directory of DFS Master instance.
   */
  public String WorkingDir;
  /**
   * List of files in DFS.
   */
  public ConcurrentHashMap<String, FileProp> FileList;
  /**
   * Available shard for each worker node.
   */
  public ConcurrentHashMap<String, List<ShardInfo>> LookupTable;

  public DfsStatus(ClusterConfig cc) {
    /* Create a `tmp` directory for holding temp files */
    this.SessionID = RandomStringUtils.randomAlphanumeric(8);
    System.out.printf("Initializing DFS session `%s`...\n", SessionID);
    new File("./tmp-DFS-" + SessionID).mkdir();
    this.WorkingDir = "./tmp-DFS-" + SessionID;
    /* Initialze empty FileList and LookupTable. */
    this.FileList = new ConcurrentHashMap<String, FileProp>();
    this.LookupTable = new ConcurrentHashMap<String, List<ShardInfo>>();
    // Initialize the shard list of each node as empty.
    for (String name : cc.getDfs().Wkrs.keySet()) {
      this.LookupTable.put(name, new ArrayList<ShardInfo>());
    }
  }
}

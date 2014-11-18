package com.yzong.dsf14.mapred.framework;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.RandomStringUtils;

import com.yzong.dsf14.mapred.dfs.FileProp;
import com.yzong.dsf14.mapred.dfs.ShardInfo;
import com.yzong.dsf14.mapred.util.ClusterConfig;

/**
 * Encapsulates all status information of the MapReduce worker node, including its local file
 * shards, Job Queue, etc.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedWorkerStatus {

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
  
  public MapRedWorkerStatus(ClusterConfig cc) {
    /* Create a `tmp` directory for holding temp files */
    this.SessionID = RandomStringUtils.randomAlphanumeric(8);
    System.out.printf("Initializing DFS session `%s`...\n", SessionID);
    new File("./tmp-DFS-" + SessionID).mkdir();
    this.WorkingDir = "./tmp-DFS-" + SessionID;
  }
}

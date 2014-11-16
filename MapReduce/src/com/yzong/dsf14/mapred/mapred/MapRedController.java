/**
 * 
 */
package com.yzong.dsf14.mapred.mapred;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import org.apache.commons.lang.RandomStringUtils;

import com.yzong.dsf14.mapred.dfs.DfsCluster;
import com.yzong.dsf14.mapred.dfs.FileProp;
import com.yzong.dsf14.mapred.dfs.ShardInfo;

/**
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedController {

  public DfsCluster DfsConfig;
  public MapRedCluster MapRedConfig;
  public String SessionID;
  public Queue<MapRedJob> JobQueue;
  public String DirPath;
  
  public MapRedController(DfsCluster dfsStatus, MapRedCluster mrStatus) {
    this.MapRedConfig = mrStatus;
    this.SessionID = RandomStringUtils.randomAlphanumeric(8);
    System.out.printf("Initializing MapReduce session `%s`...\n", SessionID);
    this.MapRedConfig = mrStatus; // Save cluster info
    this.JobQueue = new SynchronousQueue<MapRedJob>();
    /* Create a `tmp` directory for holding temp files */
    new File("./tmp-MR-" + SessionID).mkdir();
    this.DirPath = "./tmp-MR-" + SessionID;
  }

  public boolean waitForMR() {
    return false;
  }
}

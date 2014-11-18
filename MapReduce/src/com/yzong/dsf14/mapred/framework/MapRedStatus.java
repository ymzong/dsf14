package com.yzong.dsf14.mapred.framework;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.yzong.dsf14.mapred.util.ClusterConfig;

/**
 * Encapsulates all status information of the MapReduce cluster, including Job Status, Task List,
 * etc.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedStatus {

  /**
   * Global queue of all pending MapReduce jobs.
   */
  private ConcurrentLinkedQueue<MapRedJob> JobQueue;
  
  /**
   * Mapping from each node to its <i>ongoing</i> MapReduce tasks.
   */
  private ConcurrentHashMap<String, ArrayList<MapRedJob>> OngoingTasks;

  public MapRedStatus(ClusterConfig cc) {
    JobQueue = new ConcurrentLinkedQueue<MapRedJob>();
    OngoingTasks = new ConcurrentHashMap<String, ArrayList<MapRedJob>>();
    for (String name : cc.getDfs().Wkrs.keySet()) {
      OngoingTasks.put(name, new ArrayList<MapRedJob>());
    }
  }

}

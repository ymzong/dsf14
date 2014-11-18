/**
 * 
 */
package com.yzong.dsf14.mapred.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates all task info and status of a MapReduce job.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedJob implements Serializable {

  private static final long serialVersionUID = -7847881603242053845L;

  private List<MapTask> MapTasks;
  
  private List<ReduceTask> ReduceTasks;
  
  public MapRedJob() {
    MapTasks = new ArrayList<MapTask>();
    ReduceTasks = new ArrayList<ReduceTask>();
  }
}

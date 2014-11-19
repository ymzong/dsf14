package com.yzong.dsf14.mapred.framework;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract class for a Map/Reduce task on the framework. Job Tracker sends this object to the
 * worker node to start the job.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public abstract class MapRedTask implements Serializable, Comparable<MapRedTask> {

  private static final long serialVersionUID = -4989684950494437653L;
  /**
   * Global counter for Map/Reduce Tasks.
   */
  public static AtomicInteger NextId = new AtomicInteger(0);
  /**
   * Type of MapReduce Task -- Map or Reduce.
   */
  public MapRedTaskType Type;
  /**
   * Status of MapReduce Task -- Succeed, Error, Ongoing, or Pending.
   */
  public MapRedTaskStatus Status;

  public int TaskId;
  public int JobId;
  public int RetryCount;
  public String MapRedClassName;
  public String OutputDir;

  public MapRedTask(int jobId, MapRedTaskType type) {
    JobId = jobId;
    TaskId = NextId.getAndAdd(1);
    Type = type;
    RetryCount = 0;
  }

  /**
   * @return the type
   */
  public MapRedTaskType getType() {
    return Type;
  }

  /**
   * @param type the type to set
   */
  public void setType(MapRedTaskType type) {
    Type = type;
  }

  /**
   * @return the status
   */
  public MapRedTaskStatus getStatus() {
    return Status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(MapRedTaskStatus status) {
    Status = status;
  }

  /**
   * @return the taskId
   */
  public int getTaskId() {
    return TaskId;
  }

  /**
   * @param taskId the taskId to set
   */
  public void setTaskId(int taskId) {
    TaskId = taskId;
  }

  /**
   * @return the jobId
   */
  public int getJobId() {
    return JobId;
  }

  /**
   * @param jobId the jobId to set
   */
  public void setJobId(int jobId) {
    JobId = jobId;
  }

  /**
   * @return the retryCount
   */
  public int getRetryCount() {
    return RetryCount;
  }

  /**
   * @param retryCount the retryCount to set
   */
  public void setRetryCount(int retryCount) {
    RetryCount = retryCount;
  }

  /**
   * @return the mapRedClassName
   */
  public String getMapRedClassName() {
    return MapRedClassName;
  }

  /**
   * @param mapRedClassName the mapRedClassName to set
   */
  public void setMapRedClassName(String mapRedClassName) {
    MapRedClassName = mapRedClassName;
  }

  /**
   * @return the outputDir
   */
  public String getOutputDir() {
    return OutputDir;
  }

  /**
   * @param outputDir the outputDir to set
   */
  public void setOutputDir(String outputDir) {
    OutputDir = outputDir;
  }
}

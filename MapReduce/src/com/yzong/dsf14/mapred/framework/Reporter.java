package com.yzong.dsf14.mapred.framework;

/**
 * An interface for a MapReduce job to report its progress and status to the Master Scheduler.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public interface Reporter {

  /**
   * Get the health state of a task.
   * 
   * @return <tt>true</tt> iff the job is healthy.
   */
  boolean isHealthy();

  /**
   * Get the error message of the task.
   * 
   * @return Error message of task should exception occur.
   */
  String getErrorMessage();
}

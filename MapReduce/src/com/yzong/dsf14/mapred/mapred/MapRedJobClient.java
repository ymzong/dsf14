package com.yzong.dsf14.mapred.mapred;

/**
 * Interface for a user job to interact with MapReduce cluster. User program is allowed to submit
 * job, track progress, and obtain health information of the MapReduce cluster.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedJobClient {

  private MapRedJobConf JobConf = null;

  /**
   * Build a MapReduce job client with the given job configuration, and connect to the default
   * MapReduce cluster.
   * 
   * @param conf Job Configuration of the user job.
   */
  public MapRedJobClient(MapRedJobConf conf) {
    JobConf = conf;
  }

  /**
   * Runs a MapReduce job given user's <tt>JobConf</tt>.
   * 
   * @param wait <tt>True</tt> if user wishes to wait for the job to complete.
   * @return <tt>True</tt> iff the job completes successfully.
   */
  boolean run(boolean wait) {
    try {

    }
    /* Should exceptions occur, output error message and return false. */
    catch (Exception e) {
      System.err.printf("Error while running MapReduce job: %s\n", e.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Runs a MapReduce job and suspends till it completes.
   * 
   * @return <tt>True</tt> iff the job completes successfully.
   */
  boolean run() {
    return run(true);
  }
}

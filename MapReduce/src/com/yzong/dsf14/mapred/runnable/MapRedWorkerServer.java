/**
 * 
 */
package com.yzong.dsf14.mapred.runnable;

import java.net.ServerSocket;

import com.yzong.dsf14.mapred.framework.MapRedWorkerStatus;
import com.yzong.dsf14.mapred.util.ClusterConfig;

/**
 * Runnable object for server instance of MapReduce, handling Worker socket.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedWorkerServer {
  
  private ServerSocket serverSocket;
  private ClusterConfig CC;
  private MapRedWorkerStatus WS;

  public MapRedWorkerServer(ClusterConfig cc) {
    CC = cc;
    WS = new MapRedWorkerStatus(); // Initiate new worker status.
  }

  public void start() {
    // TODO Auto-generated method stub

  }

}

package com.yzong.dsf14.mapred.runnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.yzong.dsf14.mapred.dfs.DfsStatus;
import com.yzong.dsf14.mapred.framework.MapRedStatus;
import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ClusterStatus;

/**
 * Runnable object for server instance of MapReduce and underlying DFS, handling Master socket.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMasterServer {

  private ServerSocket serverSocket;
  private ClusterConfig CC;
  private ClusterStatus MS;

  /**
   * Initializes a MapRedMaster with initial status.
   * 
   * @param cc Cluster Configuration as parsed from config file.
   */
  public MapRedMasterServer(ClusterConfig cc) {
    CC = cc;
    MS = new ClusterStatus(new DfsStatus(), new MapRedStatus()); // Initiate new cluster status.
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(CC.getMr().MasterPort);
      while (true) {
        Socket socket = serverSocket.accept();
        MapRedMasterController masterController =
            new MapRedMasterController(CC, MS, new ObjectInputStream(socket.getInputStream()),
                new ObjectOutputStream(socket.getOutputStream()));
        masterController.run(); // Spins up a thread to handle the request.
      }
    } catch (IOException e) {
      /* Catch the exception, log error message, and continue runing. */
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
  }

}

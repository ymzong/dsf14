package com.yzong.dsf14.mapred.runnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.yzong.dsf14.mapred.util.ClusterConfig;

/**
 * Runnable object for server instance of MapReduce, handling Master socket.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMasterServer {

  private ServerSocket serverSocket;
  private ClusterConfig CConf;

  public MapRedMasterServer(ClusterConfig cconf) {
    CConf = cconf;
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(CConf.getMr().MasterPort);
      while (true) {
        Socket socket = serverSocket.accept();
        MapRedMasterController masterController =
            new MapRedMasterController(new ObjectInputStream(socket.getInputStream()),
                new ObjectOutputStream(socket.getOutputStream()));
        masterController.run(); // Spins up a thread to handle the request.
      }
    } catch (IOException e) {
      /* Catch the exception, log error message, and continue runing. */
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
  }

}

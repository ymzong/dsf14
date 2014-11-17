package com.yzong.dsf14.mapred.runnable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runnable object for server instance of MapReduce, handling Master socket.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedServer implements Runnable {

  private ServerSocket serverSocket;
  private ClusterConfig CConf;
  
  @Override
  public void run() {
    try {
        serverSocket = new ServerSocket(CConf.MasterPort);
        while (true) {
            Socket sock = serverSocket.accept();
            MapRedMasterController masterIO = new MapRedMasterController(sock);
            masterIO.start();
        }
    } catch (IOException e) {
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
}

}

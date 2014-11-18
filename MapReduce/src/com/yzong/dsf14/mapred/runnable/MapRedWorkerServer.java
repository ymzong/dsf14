/**
 * 
 */
package com.yzong.dsf14.mapred.runnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

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
  private int PortNum;
  
  public MapRedWorkerServer(ClusterConfig cc, int portNum) {
    CC = cc;
    WS = new MapRedWorkerStatus(cc); // Initiate new worker status.
    PortNum = portNum;
  }

  public void start() {
    try {
      serverSocket = new ServerSocket(PortNum);
      System.out.printf("INFO -- MapReduce Worker started at localhost:%d...\n", PortNum);
      while (true) {
        Socket socket = serverSocket.accept();
        MapRedWorkerController workerController =
            new MapRedWorkerController(CC, WS, new ObjectInputStream(socket.getInputStream()),
                new ObjectOutputStream(socket.getOutputStream()));
        workerController.run(); // Spins up a thread to handle the request.
      }
    } catch (IOException e) {
      /* Catch the exception, log error message, and continue runing. */
      System.out.printf("ERROR -- %s\n", e.getMessage());
    }
  }
}

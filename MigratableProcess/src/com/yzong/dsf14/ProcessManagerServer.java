package com.yzong.dsf14;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ProcessManagerServer implements Runnable {

  private int localPort;

  public ProcessManagerServer(int port) {
    this.localPort = port;
  }

  public void run() {
    System.out.printf("Starting up Process Manager at port %d...\n", localPort);
    try (ServerSocket serverSocket = new ServerSocket(localPort);
        Socket clientSocket = serverSocket.accept();
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());) {
      ServerSideProtocol ssp = new ServerSideProtocol();
      try {
        Object output = ssp.process(in.readObject());
        out.writeObject(output);
        out.flush();
      } catch (ClassNotFoundException e) {
        System.out.println("Error while processing client request.");
      }
    } catch (IOException e) {
      System.out.printf("Exception caught when binding port %d.\n", localPort);
      System.exit(1);
    }
  }
}

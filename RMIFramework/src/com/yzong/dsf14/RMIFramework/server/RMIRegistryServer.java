package com.yzong.dsf14.RMIFramework.server;

public class RMIRegistryServer implements Runnable {

  private String localHost;
  private int localPort;

  public RMIRegistryServer(String host, int port) {
    this.localHost = host;
    this.localPort = port;
  }

  @Override
  public void run() {
    System.out.printf("RMI Registry Server started at %s:%d.\n", localHost, localPort);
    
  }

}

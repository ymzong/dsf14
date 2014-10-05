package com.yzong.dsf14.RMIFramework.server;

public class RemoteObjectRef {
  private String HostName;
  private int Port;
  private String ObjHash;
  private String RemoteInterfaceName;

  public RemoteObjectRef(String host, int port, String objHash, String riName) {
    this.HostName = host;
    this.Port = port;
    this.ObjHash = objHash;
    this.RemoteInterfaceName = riName;
  }

  /**
   * This function creates a stub from a Remote Object Reference.
   * Intended to be used by the client.
   * 
   * @return Stub object corresponding to the Remote Object Reference.
   */
  Object localize() {
    return null;
  }
}

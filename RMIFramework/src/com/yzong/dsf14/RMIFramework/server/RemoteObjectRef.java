package com.yzong.dsf14.RMIFramework.server;

public class RemoteObjectRef {

  private String HostName;
  private int Port;
  private String ObjHash;
  private String RemoteInterfaceName;

  public String getHostName() {
    return HostName;
  }

  public int getPort() {
    return Port;
  }

  public String getObjHash() {
    return ObjHash;
  }

  public String getRemoteInterfaceName() {
    return RemoteInterfaceName;
  }

  public RemoteObjectRef(String host, int port, String objHash, String riName) {
    this.HostName = host;
    this.Port = port;
    this.ObjHash = objHash;
    this.RemoteInterfaceName = riName;
  }

  /**
   * Creates a stub from a Remote Object Reference. Intended to be used by the client.
   * 
   * @return Stub object corresponding to the Remote Object Reference.
   */
  Object localize() {
    return null;
  }
}

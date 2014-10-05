package com.yzong.dsf14.RMIFramework.server;

public class RemoteObjectRef {

  private String HostName;
  private int Port;
  private String ObjKey;
  private String RemoteInterfaceName;

  public String getHostName() {
    return HostName;
  }

  public int getPort() {
    return Port;
  }

  public String getObjKey() {
    return ObjKey;
  }

  public String getRemoteInterfaceName() {
    return RemoteInterfaceName;
  }

  public RemoteObjectRef(String host, int port, String objHash, String riName) {
    this.HostName = host;
    this.Port = port;
    this.ObjKey = objHash;
    this.RemoteInterfaceName = riName;
  }

  /**
   * Creates a stub from a Remote Object Reference. Intended to be used by the client.
   * 
   * @return Stub object corresponding to the Remote Object Reference.
   */
  public Object localize() {
    Class c;
    try {
      c = Class.forName(RemoteInterfaceName + "_stub");
      Object obj = c.newInstance();
      return obj;
    } catch (Exception e) {
      return null;
    }
  }
}

package com.yzong.dsf14.RMIFramework.server;

public class RemoteObjectRef {

  private String HostName;
  private int Port;
  private long ObjKey;
  private String RemoteInterfaceName;

  public String getHostName() {
    return HostName;
  }

  public int getPort() {
    return Port;
  }

  public long getObjKey() {
    return ObjKey;
  }

  public String getRemoteInterfaceName() {
    return RemoteInterfaceName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((HostName == null) ? 0 : HostName.hashCode());
    result = prime * result + (int) (ObjKey ^ (ObjKey >>> 32));
    result = prime * result + Port;
    result = prime * result + ((RemoteInterfaceName == null) ? 0 : RemoteInterfaceName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RemoteObjectRef other = (RemoteObjectRef) obj;
    if (HostName == null) {
      if (other.HostName != null)
        return false;
    } else if (!HostName.equals(other.HostName))
      return false;
    if (ObjKey != other.ObjKey)
      return false;
    if (Port != other.Port)
      return false;
    if (RemoteInterfaceName == null) {
      if (other.RemoteInterfaceName != null)
        return false;
    } else if (!RemoteInterfaceName.equals(other.RemoteInterfaceName))
      return false;
    return true;
  }

  public RemoteObjectRef(String host, int port, long objHash, String riName) {
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
    Class<? extends RMIRemoteStub> c;
    try {
      c = (Class<? extends RMIRemoteStub>) Class.forName(RemoteInterfaceName + "_stub");
      Object obj = c.newInstance();
      return obj;
    } catch (Exception e) {
      return null;
    }
  }
}

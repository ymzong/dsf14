package com.yzong.dsf14.RMIFramework.infra;

import java.io.Serializable;

/**
 * This class holds the Remote Object Reference, which contains the hostname and port number for the
 * RMI Service Host. It also contains Object Key and Remote Interface Name.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RemoteObjectRef implements Serializable {

  private static final long serialVersionUID = -5251266551341123093L;
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
   * Generates a RMI Stub Object from a Remote Object Reference. Intended to be used by the client.
   * 
   * @return RMI Stub Object corresponding to the Remote Object Reference.
   */
  public Object localize() {
    Class<?> rmiStub;
    try {
      rmiStub = (Class<?>) Class.forName(RemoteInterfaceName + "_stub");
      RMIRemoteStub obj = (RMIRemoteStub) rmiStub.newInstance();
      obj.setSelfRoR(this);
      return obj;
    } catch (Exception e) {
      /* If any error occurs, return null. */
      return null;
    }
  }

}

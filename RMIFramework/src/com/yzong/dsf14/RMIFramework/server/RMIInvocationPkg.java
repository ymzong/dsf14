package com.yzong.dsf14.RMIFramework.server;

import java.io.Serializable;

/**
 * This class contains all necessary information for an RMI function call, including the Object
 * Reference, method name, and the argument list.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIInvocationPkg implements Serializable {

  private static final long serialVersionUID = -2102320679889852805L;
  private String MethodName;
  private Object[] Args;
  private RemoteObjectRef RoR;

  public RMIInvocationPkg(String methodName, Object[] args, RemoteObjectRef remoteObjRef) {
    this.MethodName = methodName;
    this.Args = args;
    this.RoR = remoteObjRef;
  }

  public String getMethodName() {
    return MethodName;
  }

  public Object[] getArgs() {
    return Args;
  }

  public RemoteObjectRef getRoR() {
    return RoR;
  }

}

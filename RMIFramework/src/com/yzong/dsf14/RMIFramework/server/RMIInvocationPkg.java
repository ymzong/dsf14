package com.yzong.dsf14.RMIFramework.server;

/**
 * This class contains all necessary information for an RMI function call, including the Object
 * Reference, method name, and the argument list.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIInvocationPkg {
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

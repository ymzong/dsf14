package com.yzong.dsf14.RMIFramework.examples;

import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

public class CalculatorServerImpl implements CalculatorServer {

  public String CalcName = "";

  public synchronized void initialize(String name) throws RMIInvocationException {}


  public synchronized int add(int x, int y) throws RMIInvocationException {
    return x + y;
  }


  public synchronized String identify() throws RMIInvocationException {
    return CalcName;
  }


  public synchronized int secretMethod(Object srv, int x) throws RMIInvocationException {
    ZipCodeServer stub = (ZipCodeServer) srv;
    ZipCodeList list = stub.findAll();
    int count = x;
    while (list != null) {
      count++;
      list = list.next;
    }
    return count;
  }

  public synchronized void setSelfRoR(RemoteObjectRef selfRoR) {}

}

package com.yzong.dsf14.RMIFramework.examples;

import com.yzong.dsf14.RMIFramework.server.RemoteObjectRef;

/**
 * This class contains the actual implementation of <tt>ZipCodeServer</tt>, and the user essentially
 * interacts with it via the RMI framework.
 * 
 * It is not intended for local instantiation, but for being loaded by a RMI Service instance.
 * 
 * Note that <tt>synchronized</tt> modifier is added to all methods to prevent concurrency issues.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class ZipCodeServerImpl implements ZipCodeServer {
  public ZipCodeList l;

  public ZipCodeServerImpl() {
    l = null;
  }

  public synchronized void initialise(ZipCodeList newlist) {
    l = newlist;
  }

  public synchronized String find(String request) {
    ZipCodeList temp = l;
    while (temp != null && !temp.city.equals(request))
      temp = temp.next;

    if (temp == null)
      return null;
    else
      return temp.ZipCode;
  }

  public synchronized ZipCodeList findAll() {
    return l;
  }

  public synchronized void printAll() {
    ZipCodeList temp = l;
    while (temp != null) {
      System.out.println("city: " + temp.city + ", " + "code: " + temp.ZipCode + "\n");
      temp = temp.next;
    }
  }

  @Override
  public void setSelfRoR(RemoteObjectRef remoteObjectRef) {
  }

}

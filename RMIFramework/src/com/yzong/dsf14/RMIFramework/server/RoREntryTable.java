package com.yzong.dsf14.RMIFramework.server;

import java.util.Hashtable;

/**
 * This class holds all Remote Object References with a same Service Name. It is essentially a
 * Hashtable from Object Keys to RoRs.
 * 
 * @author Jimmy Zong
 */
public class RoREntryTable {

  private Hashtable<RemoteObjectRef, Object> EntryTable;
  private long ObjectCounter;
  
  public RoREntryTable() {
    this.EntryTable = new Hashtable<RemoteObjectRef, Object>();
    this.ObjectCounter = 0;
  }

  /**
   * 
   * @param hostName
   * @param port
   * @param o
   */
  public void addObj(String hostName, int port, Object o) {
    String className = o.getClass().getName();
    EntryTable.put(new RemoteObjectRef(hostName, port, ObjectCounter, className), o);
    ObjectCounter++;
  }

  public Object findObj(RemoteObjectRef ror) {
    return EntryTable.get(ror);
  }
}

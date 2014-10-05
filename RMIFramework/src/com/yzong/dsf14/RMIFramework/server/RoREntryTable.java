package com.yzong.dsf14.RMIFramework.server;

import java.util.Hashtable;

/**
 * This class holds all Remote Object References with a same Service Name. It is essentially a
 * Hashtable from Object Keys to RoRs.
 * 
 * @author Jimmy Zong
 */
public class RoREntryTable {

  private static Hashtable<RemoteObjectRef, Object> EntryTable;

  public RoREntryTable() {
    this.EntryTable = new Hashtable<RemoteObjectRef, Object>();
  }

  /**
   * 
   * @param hostName
   * @param port
   * @param o
   */
  public void addObj(Object o) {
    o.getClass();
  }

  public Object findObj(RemoteObjectRef ror) {
    return EntryTable.get(ror);
  }
}

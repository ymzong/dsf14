package com.yzong.dsf14.RMIFramework.server;

import java.util.HashMap;

/**
 * This class holds the representation of RMI Registry Table by the RMI Registry.
 * 
 * @author Jimmy Zong
 */
public class RMIRegistryTable {

  private static HashMap<String, RemoteObjectRef> regTable;

  public RMIRegistryTable() {
    this.regTable = new HashMap<String, RemoteObjectRef>();
  }
  
  public void addObj(String hostName, int port, Object o) {
    
  }
  
  public Object getObj() {
    return null;
  }
}

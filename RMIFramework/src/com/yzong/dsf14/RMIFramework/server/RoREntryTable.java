package com.yzong.dsf14.RMIFramework.server;

import java.util.Hashtable;

/**
 * This class holds all Remote Object References within a RMI Servant Host. It is essentially a
 * Hashtable from RoRs to their corresponding actual objects.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class RoREntryTable {

  private Hashtable<RemoteObjectRef, Object> EntryTable;
  private long ObjectCounter;

  public RoREntryTable() {
    this.EntryTable = new Hashtable<RemoteObjectRef, Object>();
    this.ObjectCounter = 0;
  }

  /**
   * Adds an entry into the Remote Object Reference table, given the Remote Object along with the
   * host name and port number where it is served,
   * 
   * @param hostName Host name of the RMI Servant.
   * @param port Port number of the RMI Servant.
   * @param remoteObject Remote object supported by RMI framework.
   * @return Object counter of the latest added Remote Object.
   */
  public long addObj(String hostName, int port, Object remoteObject) {
    String className = remoteObject.getClass().getName();
    EntryTable.put(new RemoteObjectRef(hostName, port, ObjectCounter, className), remoteObject);
    ObjectCounter++;
    return ObjectCounter - 1;
  }

  /**
   * Finds the Remote Object in Remote Object Reference table given its Reference.
   * 
   * @param ror Remote Object Reference to query for.
   * @return Remote object with the desired RoR; or <tt>null</tt> if not found.
   */
  public Object findObj(RemoteObjectRef ror) {
    return EntryTable.get(ror);
  }

  /**
   * Removes an entry in RoRTable given its Reference.
   * 
   * @param ror Remote Object Reference of the entry to remove.
   * @return <tt>true</tt> if entry removed; <tt>false</tt> if the entry does not exist.
   */
  public boolean removeObj(RemoteObjectRef ror) {
    return EntryTable.remove(ror) != null;
  }
}

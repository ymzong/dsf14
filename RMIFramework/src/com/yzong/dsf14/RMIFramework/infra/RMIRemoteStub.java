package com.yzong.dsf14.RMIFramework.infra;

/**
 * This interface extends the <tt>RMI Remote Object</tt> interface by forcing all stubs to have a
 * function that sets the reference of its own RoR. All stubs should implement this interface.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public interface RMIRemoteStub extends RMIRemoteObject {

  public void setSelfRoR(RemoteObjectRef selfRoR);

}

package com.yzong.dsf14.RMIFramework.infra;

/**
 * This interface extends the <tt>RMI Remote Object</tt> interface by forcing all stubs to have a
 * function that sets the reference of its own RoR. All stubs should implement this interface.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public interface RMIRemoteStub extends RMIRemoteObject {

  /**
   * Sets the <tt>selfRoR</tt> field of the Remote Object Stub to be its Remote Object Reference.
   * Useful for methods where at least one parameter is also a remote object, and we wish to pass it
   * by <i>Reference</i> instead of by <i>Value</i>.
   * 
   * @param selfRoR
   */
  public void setSelfRoR(RemoteObjectRef selfRoR);

}

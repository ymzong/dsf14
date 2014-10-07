package com.yzong.dsf14.RMIFramework.examples;

import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIRemoteStub;

/**
 * <tt>ZipCodeServer</tt> is the interface of the actual implementation of the corresponding RMI
 * Object, and it extends <tt>RMIRemoteStub</tt> for RMI Stub creation.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public interface ZipCodeServer extends RMIRemoteStub {
  /**
   * This method initializes the <tt>ZipCodeList</tt>. In RMI framework, marshalled data is sent to
   * the object and is reconstructed.
   * 
   * @param newlist <tt>ZipCodeList</tt> to load.
   * @throws RMIInvocationException 
   */
  public void initialise(ZipCodeList newlist) throws RMIInvocationException;

  /**
   * Finds the zip code of the given city.
   * 
   * @param city The city name whose zip code we wish to get.
   * @return The zip code of the given <tt>city</tt>. Returns <tt>null</tt> if city name not found.
   * @throws RMIInvocationException 
   */
  public String find(String city) throws RMIInvocationException;

  /**
   * Returns the current <tt>ZipCodeList</tt> of the object. In RMI framework, marshalled data is
   * sent to the local (calling) site.
   * 
   * @return The <tt>ZipCodeList</tt> of current object.
   * @throws RMIInvocationException 
   */
  public ZipCodeList findAll() throws RMIInvocationException;

  /**
   * Prints all <tt>(City, ZipCode)</tt> pairs on the remote site.
   * @throws RMIInvocationException 
   */
  public void printAll() throws RMIInvocationException;
}

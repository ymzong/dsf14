package com.yzong.dsf14.RMIFramework.examples;

import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIRemoteStub;

/**
 * <tt>CalculatorServer</tt> is the interface of the actual implementation of the corresponding RMI
 * Object, and it extends <tt>RMIRemoteStub</tt> for RMI Stub creation.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public interface CalculatorServer extends RMIRemoteStub {

  /**
   * Gives the Calculator instance a name.
   * 
   * @param name Name of the calculator
   * @throws RMIInvocationException
   */
  public void initialize(String name) throws RMIInvocationException;

  /**
   * Addes two given arguments together and returns their sum.
   * 
   * @param x First summand
   * @param y Second summand
   * @return Sum of two parameters
   * @throws RMIInvocationException
   */
  public int add(int x, int y) throws RMIInvocationException;

  /**
   * Returns the name of the Calculator instance.
   * 
   * @return Name of the calculator
   * @throws RMIInvocationException
   */
  public String identify() throws RMIInvocationException;

  /**
   * This method tests invoking an RMI method with another RMI object as parameter. By calling this
   * method, the RMI framework passes the parameter by <i>Reference</i> instead of by <i>Value</i>.
   * It returns the length of the ZipCodeList of the ZipCodeServer instance, plus the second
   * parameter <tt>x</tt>.
   * 
   * @param srv <tt>ZipCodeServer</tt> object whose length we wish to examine
   * @param x Value to add to the length of <tt>ZipCodeList</tt>
   * @return Length of <tt>ZipCodeList</tt> plus the second parameter
   * @throws RMIInvocationException
   */
  public int secretMethod(Object srv, int x) throws RMIInvocationException;
}

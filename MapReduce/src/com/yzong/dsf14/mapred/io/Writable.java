package com.yzong.dsf14.mapred.io;

import java.io.Serializable;

/**
 * Interface for serializable data fields used by MapReduce framework.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public interface Writable extends Serializable {

  /**
   * Parses a <tt>String</tt> data input to a Writable object.
   * 
   * @param in Input as <tt>String</tt>.
   */
  public void readFields(String in);

  /**
   * Converts the data entry to a string.
   * 
   * @return String corresponding to current value.
   */
  public String write();
}

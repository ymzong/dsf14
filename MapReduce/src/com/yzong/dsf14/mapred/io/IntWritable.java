package com.yzong.dsf14.mapred.io;

/**
 * Writable wrapper for integers.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class IntWritable implements Writable {

  private static final long serialVersionUID = 5747829611962431041L;
  private int value;

  public IntWritable(int val) {
    value = val;
  }

  @Override
  public void readFields(String in) {
    value = Integer.parseInt(in);
  }

  @Override
  public String write() {
    return "" + value;
  }

  /**
   * @return the value
   */
  public int getValue() {
    return value;
  }

  /**
   * @param value the <tt>int</tt> value to set
   */
  public void setValue(int value) {
    this.value = value;
  }

}

package com.yzong.dsf14.mapred.io;

/**
 * Writable wrapper for <tt>Long</tt>s.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class LongWritable implements Writable {

  private static final long serialVersionUID = -510212649017531802L;
  private long value;

  public LongWritable(long val) {
    value = val;
  }

  @Override
  public void readFields(String in) {
    value = Long.parseLong(in);
  }

  @Override
  public String write() {
    return "" + value;
  }

  /**
   * @return the value
   */
  public long getValue() {
    return value;
  }

  /**
   * @param value the <tt>long</tt> value to set
   */
  public void setValue(long value) {
    this.value = value;
  }

}

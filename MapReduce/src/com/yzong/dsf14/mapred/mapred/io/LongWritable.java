package com.yzong.dsf14.mapred.mapred.io;

/**
 * Writable wrapper for integers.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class LongWritable implements Writable {

  private static final long serialVersionUID = 5747829611962431041L;
  private long value;

  @Override
  public void readFields(String in) {
    value = Long.parseLong(in);
  }

  @Override
  public String write() {
    return "" + value;
  }

  public long getValue() {
    return value;
  }


}

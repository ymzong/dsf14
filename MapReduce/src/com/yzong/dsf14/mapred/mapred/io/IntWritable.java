/**
 * 
 */
package com.yzong.dsf14.mapred.mapred.io;

/**
 * Writable wrapper for integers.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class IntWritable implements Writable {

  private static final long serialVersionUID = 5747829611962431041L;
  private int value;

  @Override
  public void readFields(String in) {
    value = Integer.parseInt(in);
  }

  @Override
  public String write() {
    return "" + value;
  }

  public int getValue() {
    return value;
  }


}

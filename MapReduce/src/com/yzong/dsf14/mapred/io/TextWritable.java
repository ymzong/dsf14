package com.yzong.dsf14.mapred.io;

/**
 * Writable wrapper for text/strings.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class TextWritable implements Writable {

  private static final long serialVersionUID = -9219001075105608183L;
  private String value;

  public TextWritable(String val) {
    value = val;
  }

  @Override
  public void readFields(String in) {
    value = in;
  }

  @Override
  public String write() {
    return value;
  }

  /**
   * @return value The value that <tt>TextWritable</tt> is holding.
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value The value that <tt>TextWritable</tt> is to hold.
   */
  public void setValue(String value) {
    this.value = value;
  }

}

package com.yzong.dsf14.mapred.mapred.io;

public class TextWritable implements Writable {

  private static final long serialVersionUID = -9219001075105608183L;
  private String value;

  @Override
  public void readFields(String in) {
    value = in;
  }

  @Override
  public String write() {
    return value;
  }

  public String getValue() {
    return value;
  }

}

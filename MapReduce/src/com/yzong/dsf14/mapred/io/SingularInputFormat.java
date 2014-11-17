package com.yzong.dsf14.mapred.io;

/**
 * An input format where each line is viewed as a separate record containing only the "value" field.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class SingularInputFormat implements InputFormat {

  public Class<?> LineWriter;

  public SingularInputFormat(Class<?> type) {
    LineWriter = type;
  }

}

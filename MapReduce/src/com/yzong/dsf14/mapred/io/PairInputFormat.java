package com.yzong.dsf14.mapred.io;

/**
 * An input format where each line is viewed as a key-value pair with respective <tt>Writer</tt>s.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class PairInputFormat implements InputFormat {
  
  public Class<?> KeyLineWriter;
  public Class<?> ValLineWriter;

  public PairInputFormat(Class<?> keyType, Class<?> valType) {
    KeyLineWriter = keyType;
    ValLineWriter = valType;
  }

}

package com.yzong.dsf14.mapred.util;

import java.io.Serializable;

/**
 * Communication package for MapReduce cluster.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMessage implements Serializable {

  private static final long serialVersionUID = 5106476037363707041L;
  public String Command;
  public Object Body;

  /**
   * Default case, create an error package.
   */
  public MapRedMessage() {
    Command = "XXX";
    Body = "N/A";
  }

  public MapRedMessage(String command, Object body) {
    Command = command;
    Body = body;
  }

}

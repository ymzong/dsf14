package com.yzong.dsf14.mapred.dfs;

import java.io.Serializable;

/**
 * Communication package from a client to DFS Master to deliver request and response.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsRequestPkg implements Serializable {

  private static final long serialVersionUID = -2854992595078329774L;
  public String Command;
  public Object Body;

  /**
   * @param command
   * @param body
   */
  public DfsRequestPkg(String command, Object body) {
    Command = command;
    Body = body;
  }

}

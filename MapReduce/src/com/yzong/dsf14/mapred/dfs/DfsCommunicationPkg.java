package com.yzong.dsf14.mapred.dfs;

import java.io.Serializable;

/**
 * Communication package <i>between</i> DFS nodes (Master/Worker).
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsCommunicationPkg implements Serializable {

  private static final long serialVersionUID = 175508092255213493L;
  public String Command;
  public Object Body;

  public DfsCommunicationPkg(String command, Object body) {
    Command = command;
    Body = body;
  }

}

package com.yzong.dsf14.mapred.dfs;

/**
 * Communication package between DFS nodes (Master/Worker).
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsCommunicationPkg {
  public String Command;
  public Object Body;

  public DfsCommunicationPkg(String command, Object body) {
    Command = command;
    Body = body;
  }

}

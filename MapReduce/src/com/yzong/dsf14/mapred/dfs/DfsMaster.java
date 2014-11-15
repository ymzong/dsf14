package com.yzong.dsf14.mapred.dfs;

import com.yzong.dsf14.mapred.util.ConfigManager;


/**
 * Entry point for the distributed file system Master CLI. It supports Read/Write operations issued
 * by the user on the command line.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class DfsMaster {

  public final static String CFG_PATH = "conf/cluster.xml";

  /**
   * Entry point for DFS Master CLI.
   * 
   * @param args Not used.
   */
  public static void main(String[] args) {
    ConfigManager cm = new ConfigManager(CFG_PATH);
    cm.parseDFSConfig();
    System.out.printf("Config file loaded from %s!\n", CFG_PATH);
  }

}

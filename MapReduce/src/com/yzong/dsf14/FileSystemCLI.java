package com.yzong.dsf14;

/**
 * Entry point for the distributed file system used by MapReduce. It supports Read/Write operations,
 * issued on the command line.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 */
public class FileSystemCLI {

  /**
   * @param args
   */
  public static void main(String[] args) {
    ConfigManager cm = new ConfigManager("conf/cluster.xml");
    /* TOOD: Complete the CLI for Distributed FS. */
    cm.generateConfig();
    cm.parseConfig();
  }

}

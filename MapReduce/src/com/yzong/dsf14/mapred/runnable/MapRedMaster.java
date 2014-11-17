package com.yzong.dsf14.mapred.runnable;

import com.yzong.dsf14.mapred.dfs.DfsConfig;
import com.yzong.dsf14.mapred.dfs.DfsController;
import com.yzong.dsf14.mapred.framework.MapRedConfig;
import com.yzong.dsf14.mapred.framework.MapRedController;
import com.yzong.dsf14.mapred.util.ConfigManager;

/**
 * Entry point for MapReduce framework Master CLI. Deceptively though, it mainly allows users to
 * load data into the underlying Distributed File System, and pull distributed files back to a
 * single piece on local File System.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMaster {

  public static void main(String[] args) {
    /* Load parameters from Config File. Verify participants. */
    if (args.length < 1) {
      System.err.println("Please enter the path to config file in the first parameter!");
    }
    final String CFG_PATH = args[0];
    ConfigManager cm = new ConfigManager(CFG_PATH);
    DfsConfig dfsStatus = cm.parseDFSConfig();
    MapRedConfig mrStatus = cm.parseMRConfig();
    if (dfsStatus == null || mrStatus == null) {
      System.out.println("Please fix your config file and try again!");
      System.exit(1);
    }
    System.out.printf("Config file loaded from %s!\n", CFG_PATH);
    DfsController DC = new DfsController(dfsStatus);
    DC.waitForDFS();
    MapRedController MC = new MapRedController(dfsStatus, mrStatus);
    MC.waitForMR();
  }

}

package com.yzong.dsf14.mapred.util;

import java.io.Console;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.yzong.dsf14.mapred.dfs.DfsCluster;
import com.yzong.dsf14.mapred.dfs.DfsWorkerInfo;
import com.yzong.dsf14.mapred.framework.MapRedCluster;
import com.yzong.dsf14.mapred.framework.MapRedWorkerInfo;

/**
 * Contains utility functions that generate default configuration file for DFS/MapRed cluster and
 * parse a configuration file.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ConfigManager {
  public String PathName;
  public DfsCluster DFSClusterStatus;
  public MapRedCluster MRClusterStatus;

  public ConfigManager(String pathName) {
    this.PathName = pathName;
  }

  /**
   * Generates a config file template for app developer to modify.
   */
  public void generateConfig() {
    XMLConfiguration config = new XMLConfiguration();
    /* Set general properties. */
    config.setRootElementName("MapReduceConfig");
    config.addProperty("MapPerHostPerCore", 1.5);
    config.addProperty("ReducePerHostPerCore", 1.0);
    config.addProperty("DFSShardSize", 1000);
    config.addProperty("DFSReplication", 3);
    /* Set master properties. */
    config.addProperty("Master.host", "ghc35.ghc.andrew.cmu.edu");
    config.addProperty("Master.fsport", 9192);
    config.addProperty("Master.mrport", 9193);
    /* Set worker properties. */
    config.addProperty("Workers.Worker(0).host", "unix3.andrew.cmu.edu");
    config.addProperty("Workers.Worker(1).host", "unix5.andrew.cmu.edu");
    config.addProperty("Workers.Worker(2).host", "unix1.andrew.cmu.edu");
    config.addProperty("Workers.Worker(0).fsport", 9192);
    config.addProperty("Workers.Worker(1).fsport", 9192);
    config.addProperty("Workers.Worker(2).fsport", 9192);
    config.addProperty("Workers.Worker(0).mrport", 9193);
    config.addProperty("Workers.Worker(1).mrport", 9193);
    config.addProperty("Workers.Worker(2).mrport", 9193);
    config.addProperty("Workers.Worker(0).cores", 6);
    config.addProperty("Workers.Worker(1).cores", 10);
    config.addProperty("Workers.Worker(2).cores", 6);
    config.addProperty("Workers.Worker(0).name", "unix3");
    config.addProperty("Workers.Worker(1).name", "unix5");
    config.addProperty("Workers.Worker(2).name", "unix1");
    /* Save config file. */
    try {
      config.save(new File(PathName));
    } catch (ConfigurationException e) {
      System.err.printf("Error saving file to %s!\n", PathName);
    }
    return;
  }

  /**
   * Parses the configuration file for DFS cluster information. If exception occurs, allow user to
   * create default XML as template.
   * 
   * @return Parsed <tt>DSFCluster</tt> object containing cluster information.
   */
  public DfsCluster parseDFSConfig() {
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(PathName);
      /* Parse master/worker address and identifier. */
      String MasterHost = (String) config.getProperty("Master.host");
      int MasterPort = Integer.parseInt((String) config.getProperty("Master.fsport"));
      int ShardSize = Integer.parseInt((String) config.getProperty("DFSShardSize"));
      int Replication = Integer.parseInt((String) config.getProperty("DFSReplication"));
      List<HierarchicalConfiguration> Workers = config.configurationsAt("Workers.Worker");
      HashMap<String, DfsWorkerInfo> WorkerInfo = new HashMap<String, DfsWorkerInfo>();
      for (HierarchicalConfiguration w : Workers) {
        DfsWorkerInfo worker = new DfsWorkerInfo(w.getString("host"), w.getInt("fsport"));
        WorkerInfo.put(w.getString("name"), worker);
      }
      /* Create ClusterConfig object and return to CLI. */
      DfsCluster cc = new DfsCluster(MasterHost, MasterPort, ShardSize, Replication, WorkerInfo);
      this.DFSClusterStatus = cc;
      return cc;
    }
    /* Upon parsing error, allow user to create a default XML instead. */
    catch (Exception e) {
      System.err.println("Error while loading configuration...");
      Console console = System.console();
      if (!console.readLine("Want to generate a default config at denoted location? (N)")
          .toLowerCase().startsWith("y")) {
        generateConfig();
      }
    }
    return null;
  }

  /**
   * Parses the configuration file for MapReduce cluster information. If exception occurs, allow
   * user to create default XML as template.
   * 
   * @return Parsed <tt>MRCluster</tt> object containing cluster information.
   */
  public MapRedCluster parseMRConfig() {
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(PathName);
      /* Parse max mapper/reducer per core. */
      double MapPerHostPerCore =
          Double.parseDouble((String) config.getProperty("MapPerHostPerCore"));
      double ReducePerHostPerCore =
          Double.parseDouble((String) config.getProperty("ReducePerHostPerCore"));
      /* Parse master/worker address and identifier. */
      String MasterHost = (String) config.getProperty("Master.host");
      int MasterPort = Integer.parseInt((String) config.getProperty("Master.mrport"));
      List<HierarchicalConfiguration> Workers = config.configurationsAt("Workers.Worker");
      HashMap<String, MapRedWorkerInfo> WorkerInfo = new HashMap<String, MapRedWorkerInfo>();
      for (HierarchicalConfiguration w : Workers) {
        MapRedWorkerInfo worker =
            new MapRedWorkerInfo(w.getString("host"), w.getInt("mrport"),
                (int) Math.floor(MapPerHostPerCore * w.getInt("cores")),
                (int) Math.floor(ReducePerHostPerCore * w.getInt("cores")));
        WorkerInfo.put(w.getString("name"), worker);
      }
      /* Create ClusterConfig object and return to CLI. */
      MapRedCluster cc = new MapRedCluster(MasterHost, MasterPort, WorkerInfo);
      this.MRClusterStatus = cc;
      return cc;
    }
    /* Upon parsing error, allow user to create a default XML instead. */
    catch (Exception e) {
      System.err.println("Error while loading configuration...");
      Console console = System.console();
      if (!console.readLine("Want to generate a default config at denoted location? (N)")
          .toLowerCase().startsWith("y")) {
        generateConfig();
      }
    }
    return null;
  }
}

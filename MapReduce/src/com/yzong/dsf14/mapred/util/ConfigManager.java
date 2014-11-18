package com.yzong.dsf14.mapred.util;

import java.io.Console;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import com.yzong.dsf14.mapred.dfs.DfsConfig;
import com.yzong.dsf14.mapred.dfs.DfsWorkerConfig;
import com.yzong.dsf14.mapred.framework.MapRedConfig;
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
  public DfsConfig DFSClusterConfig;
  public MapRedConfig MRClusterConfig;

  public ConfigManager(String pathName) {
    PathName = pathName;
    DFSClusterConfig = parseDFSConfig();
    MRClusterConfig = parseMRConfig();
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
  public DfsConfig parseDFSConfig() {
    XMLConfiguration config = null;
    try {
      config = new XMLConfiguration(PathName);
      /* Parse master/worker address and identifier. */
      String MasterHost = (String) config.getProperty("Master.host");
      int MasterPort = Integer.parseInt((String) config.getProperty("Master.fsport"));
      int ShardSize = Integer.parseInt((String) config.getProperty("DFSShardSize"));
      int Replication = Integer.parseInt((String) config.getProperty("DFSReplication"));
      List<HierarchicalConfiguration> Workers = config.configurationsAt("Workers.Worker");
      HashMap<String, DfsWorkerConfig> WorkerInfo = new HashMap<String, DfsWorkerConfig>();
      for (HierarchicalConfiguration w : Workers) {
        DfsWorkerConfig worker = new DfsWorkerConfig(w.getString("host"), w.getInt("fsport"));
        WorkerInfo.put(w.getString("name"), worker);
      }
      /* Create ClusterConfig object and return to CLI. */
      DfsConfig cc = new DfsConfig(MasterHost, MasterPort, ShardSize, Replication, WorkerInfo);
      this.DFSClusterConfig = cc;
      return cc;
    }
    /* Upon parsing error, allow user to create a default XML instead. */
    catch (Exception e) {
      System.err.println("Error while loading DFS configuration...");
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
  public MapRedConfig parseMRConfig() {
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
      MapRedConfig cc = new MapRedConfig(MasterHost, MasterPort, WorkerInfo);
      this.MRClusterConfig = cc;
      return cc;
    }
    /* Upon parsing error, allow user to create a default XML instead. */
    catch (Exception e) {
      System.err.println("Error while loading MR configuration...");
      Console console = System.console();
      if (!console.readLine("Want to generate a default config at denoted location? (N)")
          .toLowerCase().startsWith("y")) {
        generateConfig();
      }
    }
    return null;
  }

  /**
   * Waits for all worker nodes in DFS to be ready.
   * 
   * @return <tt>true</tt> iff the cluster initialized successfully.
   */
  public boolean waitForCluster() {
    List<String> unavailableWorker = new ArrayList<String>(DFSClusterConfig.Wkrs.keySet());
    while (unavailableWorker.size() != 0) {
      System.out.printf("\nWaiting for worker nodes... (%d remaining)\n", unavailableWorker.size());
      for (Iterator<String> i = unavailableWorker.iterator(); i.hasNext();) {
        String w = i.next();
        Socket outSocket;
        try {
          outSocket =
              new Socket(DFSClusterConfig.Wkrs.get(w).HostName,
                  DFSClusterConfig.Wkrs.get(w).PortNum);
          ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
          out.writeObject(new MapRedMessage("PING", null));
          String response = ((MapRedMessage) in.readObject()).Command;
          outSocket.close();
          if (response.equals("PONG")) {
            i.remove();
          }
        }
        /* If connection error occurs, ignore and continue. */
        catch (Exception e) {
          System.out.printf("Warning: Worker %s:%d not reachable! Will retry in 2 seconds...\n",
              DFSClusterConfig.Wkrs.get(w).HostName, DFSClusterConfig.Wkrs.get(w).PortNum);
        }
      }
      try {
        Thread.sleep(2000); // Waits for 2 seconds before another round.
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
    System.out.println("\nCluster initialized successfully.");
    return true;
  }

  /**
   * Wrapper function for <tt>parseDFSConfig</tt> and <tt>parseMRConfig</tt>. Returns the
   * configuration of an entire cluster.
   * 
   * @param wait Wait for everything to come up (as master)
   * @return
   */
  public ClusterConfig verifyConfig(boolean wait) {
    if (DFSClusterConfig == null || MRClusterConfig == null) {
      return null;
    }
    if (!wait || waitForCluster()) {
      return new ClusterConfig(DFSClusterConfig, MRClusterConfig);
    } else {
      return null;
    }
  }
}

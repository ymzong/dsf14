package com.yzong.dsf14;

import java.io.Console;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Generates default configuration file for cluster, and parses a configuration file.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ConfigManager {
  public String PathName;

  public ConfigManager(String pathName) {
    this.PathName = pathName;
  }

  /**
   * Generates a config file template for users to modify.
   */
  void generateConfig() {
    XMLConfiguration config = new XMLConfiguration();
    /* Set general properties. */
    config.setRootElementName("MapReduceConfig");
    config.addProperty("MapPerHostPerCore", 1.5);
    config.addProperty("ReducePerHostPerCore", 1);
    /* Set master properties. */
    config.addProperty("Master.host", "ghc35.ghc.andrew.cmu.edu");
    config.addProperty("Master.port", 9192);
    /* Set worker properties. */
    config.addProperty("Workers.Worker(0).host", "unix3.andrew.cmu.edu");
    config.addProperty("Workers.Worker(1).host", "unix5.andrew.cmu.edu");
    config.addProperty("Workers.Worker(2).host", "unix1.andrew.cmu.edu");
    config.addProperty("Workers.Worker(0).port", 9192);
    config.addProperty("Workers.Worker(1).port", 9192);
    config.addProperty("Workers.Worker(2).port", 9192);
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
   * Parses the configuration file for cluster information. If exception occurs, allow user to
   * create default XML as template.
   * 
   * @return Parsed ClusterConfig containing cluster information.
   */
  ClusterConfig parseConfig() {
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
      int MasterPort = Integer.parseInt((String) config.getProperty("Master.port"));
      List<HierarchicalConfiguration> Workers = config.configurationsAt("Workers.Worker");
      HashMap<String, WorkerInfo> WorkerInfo = new HashMap<String, WorkerInfo>();
      for (HierarchicalConfiguration w : Workers) {
        WorkerInfo worker =
            new WorkerInfo(w.getString("hostname"), w.getInt("port"),
                (int) Math.floor(MapPerHostPerCore * w.getInt("cores")),
                (int) Math.floor(ReducePerHostPerCore * w.getInt("cores")));
        WorkerInfo.put(w.getString("name"), worker);
      }
      /* Create ClusterConfig object and return to CLI. */
      ClusterConfig cc = new ClusterConfig(MasterHost, MasterPort, WorkerInfo);
      return cc;
    }
    /* Upon parsing error, allow user to create a default XML instead. */
    catch (ConfigurationException e) {
      System.err.println("Error while loading configuration...");
      Console console = System.console();
      if (!console.readLine("Want to generate a default config at denoted location? (Y)")
          .toLowerCase().startsWith("n")) {
        generateConfig();
      }
    }
    return null;
  }

}

package com.yzong.dsf14.mapred.runnable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ConfigManager;

/**
 * Main entry point for all utilities supported by the MapReduce framework.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class JimmyMapRed {

  /**
   * Displays help message to use in case of invalid arguments.
   * 
   * @param ops <tt>Options</tt> object for Apache Commons CLI library.
   */
  private static void displayHelp(Options ops) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java -jar JimmyMapRed.jar <args>", ops);
    System.exit(1);
  }

  /**
   * Builds command-line argument group.
   * 
   * @return CLI Options group for MapReduce.
   */
  private static OptionGroup buildOpsGroup() {
    OptionGroup optionGroup = new OptionGroup();

    OptionBuilder.hasArgs();
    OptionBuilder.withDescription("starts MapReduce & DFS master on current node");
    Option startMasterOp = OptionBuilder.create("StartMaster");
    optionGroup.addOption(startMasterOp);

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("PortNum");
    OptionBuilder.withDescription("starts worker node on current node at certain port");
    Option startWorkerOp = OptionBuilder.create("StartWorker");
    optionGroup.addOption(startWorkerOp);

    OptionBuilder.hasArgs();
    OptionBuilder.withDescription("destroys currently running MapReduce & DFS cluster");
    Option destroyClusterOp = OptionBuilder.create("DestroyCluster");
    optionGroup.addOption(destroyClusterOp);

    OptionBuilder.hasArgs();
    OptionBuilder.withDescription("lists all jobs in current cluster");
    Option listJobsOp = OptionBuilder.create("ListJobs");
    optionGroup.addOption(listJobsOp);

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("JobID");
    OptionBuilder.withDescription("polls status of a certain MapReduce job");
    Option poolJobOp = OptionBuilder.create("PollJob");
    optionGroup.addOption(poolJobOp);

    OptionBuilder.hasArgs(2);
    OptionBuilder.withArgName("JarPath> <ClsPath");
    OptionBuilder.withDescription("runs certain MapReduce job in jar");
    Option runJobOp = OptionBuilder.create("RunJob");
    optionGroup.addOption(runJobOp);

    return optionGroup;
  }

  /**
   * Main function that parses command-line arguments and perform corresponding tasks.
   * 
   * @param args Command-line arguments.
   */
  public static void main(String[] args) {
    /* Building command-line options. */
    Options cliOptions = new Options();
    cliOptions.addOptionGroup(buildOpsGroup());
    cliOptions.addOption("Conf", true, "path name for config file");
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed.  Reason -- " + e.getMessage());
      displayHelp(cliOptions);
    }
    /* Parsing CLI arguments and Config file. */
    ClusterConfig CC = new ConfigManager(cmd.getOptionValue("Conf")).parseConfig();
    /* Case One: Start up new cluster. */
    if (cmd.hasOption("StartMaster")) {
      /* Spin up DFS Master. */
      // TODO: Start DFS server -- *passive mode*
      /* Spin up MapRed cluster Master node on top of DFS. */
      MapRedMasterServer mrMasterServer = new MapRedMasterServer(CC);
      mrMasterServer.start();
    } else if (cmd.hasOption("StartWorker")) {
      /* Spin up DFS Worker. */
      // TODO: Start DFS server -- *passive mode*
      /* Spin up MapRed cluster Worker node on top of DFS. */
      MapRedWorkerServer mrWorkerServer = new MapRedWorkerServer(CC);
      mrWorkerServer.start();
    } else if (cmd.hasOption("DestroyCluster")) {
    } else if (cmd.hasOption("ListJobs")) {
    } else if (cmd.hasOption("PollJob")) {
    } else if (cmd.hasOption("RunJob")) {
    } else {
      displayHelp(cliOptions);
    }
  }
}

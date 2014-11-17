package com.yzong.dsf14.mapred.runnable;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

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
   * @return CLI Options group.
   */
  private static OptionGroup buildOpsGroup() {
    OptionGroup optionGroup = new OptionGroup();

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("CfgPath");
    OptionBuilder.withDescription("starts MapReduce & DFS master on current node");
    Option startMasterOp = OptionBuilder.create("StartMaster");
    optionGroup.addOption(startMasterOp);

    OptionBuilder.hasArgs(2);
    OptionBuilder.withArgName("CfgPath> <PortNum");
    OptionBuilder.withDescription("starts worker node on current node at certain port");
    Option startWorkerOp = OptionBuilder.create("StartWorker");
    optionGroup.addOption(startWorkerOp);

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("CfgPath");
    OptionBuilder.withDescription("destroys currently running MapReduce & DFS cluster");
    Option destroyClusterOp = OptionBuilder.create("DestroyCluster");
    optionGroup.addOption(destroyClusterOp);

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("CfgPath");
    OptionBuilder.withDescription("lists all jobs in current cluster");
    Option listJobsOp = OptionBuilder.create("ListJobs");
    optionGroup.addOption(listJobsOp);

    OptionBuilder.hasArgs(2);
    OptionBuilder.withArgName("CfgPath> <JobID");
    OptionBuilder.withDescription("polls status of a certain MapReduce job");
    Option poolJobOp = OptionBuilder.create("PollJob");
    optionGroup.addOption(poolJobOp);

    OptionBuilder.hasArgs(3);
    OptionBuilder.withArgName("CfgPath> <JarPath> <ClsPath");
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
    /* Creating command-line options. */
    Options cliOptions = new Options();
    cliOptions.addOptionGroup(buildOpsGroup());
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed.  Reason: " + e.getMessage());
      displayHelp(cliOptions);
    }
    if (cmd.hasOption("StartMaster")) {
      
    }
    else if (cmd.hasOption("StartWorker")) {
      
    }
    else if (cmd.hasOption("DestroyCluster")) {
      
    }
    else if (cmd.hasOption("ListJobs")) {
      
    }
    else if (cmd.hasOption("PollJob")) {
      
    }
    else if (cmd.hasOption("RunJob")) {
      
    } else {
      displayHelp(cliOptions);
    }
  }
}

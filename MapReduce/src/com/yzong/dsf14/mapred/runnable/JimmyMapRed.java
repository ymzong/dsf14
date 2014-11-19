package com.yzong.dsf14.mapred.runnable;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;

import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ConfigManager;
import com.yzong.dsf14.mapred.util.MapRedMessage;

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

    OptionBuilder.hasArgs(0);
    OptionBuilder.withDescription("starts MapReduce & DFS master on current node");
    Option startMasterOp = OptionBuilder.create("StartMaster");
    optionGroup.addOption(startMasterOp);

    OptionBuilder.hasArgs(1);
    OptionBuilder.withArgName("PortNum");
    OptionBuilder.withDescription("starts worker node on current node at certain port");
    Option startWorkerOp = OptionBuilder.create("StartWorker");
    optionGroup.addOption(startWorkerOp);

    OptionBuilder.hasArgs(0);
    OptionBuilder.withDescription("destroys currently running MapReduce & DFS cluster");
    Option destroyClusterOp = OptionBuilder.create("DestroyCluster");
    optionGroup.addOption(destroyClusterOp);

    OptionBuilder.hasArgs(0);
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

    OptionBuilder.hasArgs(2);
    OptionBuilder.withArgName("localPath> <remotePath");
    OptionBuilder.withDescription("loads local file onto DFS");
    Option loadFileOp = OptionBuilder.create("LoadFile");
    optionGroup.addOption(loadFileOp);

    OptionBuilder.hasArgs(2);
    OptionBuilder.withArgName("remotePath> <localPath");
    OptionBuilder.withDescription("pulls DFS file onto local file system");
    Option pullFileOp = OptionBuilder.create("PullFile");
    optionGroup.addOption(pullFileOp);

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
    cliOptions.addOption("Conf", true, "(optional) path name for config file");
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed.  Reason -- " + e.getMessage());
      displayHelp(cliOptions);
    }
    /* Parsing CLI arguments and Config file. */
    String confPath = cmd.getOptionValue("Conf");
    if (confPath == null) {
      System.out
          .println("Path to config file not found -- using default value: `conf/cluster.xml`...");
      confPath = "conf/cluster.xml";
    }
    ClusterConfig CC = new ConfigManager(confPath).verifyConfig(false);

    /* Case One: Start up new master node. */
    if (cmd.hasOption("StartMaster")) {
      new ConfigManager(confPath).verifyConfig(true); // Wait for worker nodes to come up.
      MapRedMasterServer mrMasterServer = new MapRedMasterServer(CC);
      mrMasterServer.start();
    }
    /* Case Two: Start up new worker node. */
    else if (cmd.hasOption("StartWorker")) {
      MapRedWorkerServer mrWorkerServer =
          new MapRedWorkerServer(CC, Integer.parseInt(cmd.getOptionValues("StartWorker")[0]));
      mrWorkerServer.start();
    }
    /* Case Three: Destroy current cluster. */
    else if (cmd.hasOption("DestroyCluster")) {
      try {
        Socket outSocket = new Socket(CC.getDfs().MasterHost, CC.getDfs().MasterPort);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(new MapRedMessage("ALL/DESTROY", null));
        MapRedMessage response = (MapRedMessage) in.readObject();
        outSocket.close();
        if (((String) response.Command).equals("OK")) {
          System.out.println("Successfully terminated cluster!");
        } else {
          System.out.printf("Error while performing task...\nException -- %s\n",
              (String) response.Body);
        }
      } catch (Exception e) {
        System.out.printf(
            "Error connecting to cluster... (Has cluster started?)\nException -- %s\n",
            e.getMessage());
      }
    }
    /* Case Four: List all jobs in cluster. */
    else if (cmd.hasOption("ListJobs")) {
      try {
        Socket outSocket = new Socket(CC.getMr().MasterHost, CC.getDfs().MasterPort);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(new MapRedMessage("MR/LISTJOBS", null));
        MapRedMessage response = (MapRedMessage) in.readObject();
        outSocket.close();
        if (((String) response.Command).equals("OK")) {
          // TODO: Print out jobs.
        } else {
          System.out.printf("Error while performing task...\nException -- %s\n",
              (String) response.Body);
        }
      } catch (Exception e) {
        System.out.printf(
            "Error connecting to cluster... (Has cluster started?)\nException -- %s\n",
            e.getMessage());
      }
    }
    /* Case Five: Poll the status of ongoing job. */
    else if (cmd.hasOption("PollJob")) {
      try {
        Socket outSocket = new Socket(CC.getDfs().MasterHost, CC.getDfs().MasterPort);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        out.writeObject(new MapRedMessage("MR/POLLJOB", cmd.getOptionValues("PollJob")[0]));
        MapRedMessage response = (MapRedMessage) in.readObject();
        outSocket.close();
        if (((String) response.Command).equals("OK")) {
          // TODO: Print out job status.
        } else {
          System.out.printf("Error while performing task...\nException -- %s\n",
              (String) response.Body);
        }
      } catch (Exception e) {
        System.out.printf(
            "Error connecting to cluster... (Has cluster started?)\nException -- %s\n",
            e.getMessage());
      }
    }
    /* Case Six: Run MapReduce job on cluster. */
    else if (cmd.hasOption("RunJob")) {
      // TODO: RunJob.
    }
    /* Case Seven: Load local file to DFS. */
    else if (cmd.hasOption("LoadFile")) {
      try {
        Socket outSocket = new Socket(CC.getDfs().MasterHost, CC.getDfs().MasterPort);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        String localPath = cmd.getOptionValues("LoadFile")[0];
        String remotePath = cmd.getOptionValues("LoadFile")[1];
        out.writeObject(new MapRedMessage("DFS/LOAD", new Object[] {remotePath,
            FileUtils.readFileToByteArray(new File(localPath))}));
        MapRedMessage response = (MapRedMessage) in.readObject();
        outSocket.close();
        if (((String) response.Command).equals("OK")) {
          System.out.printf("File `%s` successfully loaded on JimmyDFS as `%s`.\n", localPath,
              remotePath);
        } else {
          System.out.printf("Error while performing task...\nException -- %s\n",
              (String) response.Body);
        }
      } catch (Exception e) {
        System.out.printf(
            "Error connecting to cluster... (Has cluster started?)\nException -- %s\n",
            e.getMessage());
      }
    }
    /* Case Eight: Pull file on DFS to local. */
    else if (cmd.hasOption("PullFile")) {
      try {
        Socket outSocket = new Socket(CC.getDfs().MasterHost, CC.getDfs().MasterPort);
        ObjectOutputStream out = new ObjectOutputStream(outSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(outSocket.getInputStream());
        String remotePath = cmd.getOptionValues("PullFile")[0];
        String localPath = cmd.getOptionValues("PullFile")[1];
        out.writeObject(new MapRedMessage("DFS/PULL", new Object[] {remotePath, localPath}));
        MapRedMessage response = (MapRedMessage) in.readObject();
        outSocket.close();
        if (((String) response.Command).equals("OK")) {
          System.out.printf("File `%s` successfully pulled to `%s`\n", remotePath, localPath);
        } else {
          System.out.printf("Error while performing task...\nException -- %s\n",
              (String) response.Body);
        }
      } catch (Exception e) {
        System.out.printf(
            "Error connecting to cluster... (Has cluster started?)\nException -- %s\n",
            e.getMessage());
      }
    }
    /* Case Nine: Unrecognized command. */
    else {
      displayHelp(cliOptions);
    }
  }
}

package com.yzong.dsf14.RMIFramework.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class contains the entry point of <tt>RMI Registry Server</tt>. It parses command-line
 * arguments and starts the Registry in another thread.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIRegistry {

  /**
   * Use Apache Commons CLI Library to display help message to the user.
   * 
   * @param ops Options list
   */
  static void displayHelp(Options ops) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java -jar RMIRegistry.jar -p port_num", ops);
    System.exit(1);
  }

  /**
   * Entry point for RMI Registry Server.
   * 
   * @param args Should be "<tt>-p portNum</tt>".
   * @throws UnknownHostException When the local host name cannot be obtained.
   */
  public static void main(String[] args) throws UnknownHostException {
    Options cliOptions = new Options();
    cliOptions.addOption("p", true, "Port for RMI Registry");
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed. Reason: " + e.getMessage());
      displayHelp(cliOptions);
    }
    String localhost = InetAddress.getLocalHost().getHostName();
    int localport = -1;
    if (cmd.hasOption("p") && NumberUtils.isNumber(cmd.getOptionValue("p"))) {
      localport = Integer.parseInt(cmd.getOptionValue("p"));
      RMIRegistryServer rmisrv = new RMIRegistryServer(localhost, localport);
      Thread rmisrvThread = new Thread(rmisrv);
      rmisrvThread.start();
      return;
    }
    // Invalid CLI arguments.
    displayHelp(cliOptions);
    return;
  }

}

package com.yzong.dsf14;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class ProcessManagerCLI {

  static void displayHelp(Options ops) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java -jar ProcessManager {-m|-c} -p port_num", ops);
    System.exit(1);
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws UnknownHostException {
    Options cliOptions = new Options();
    cliOptions.addOption("m", false, "run master on current node");
    cliOptions.addOption("c", false, "run child on current node");
    cliOptions.addOption("p", true, "port for current node");
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed.  Reason: " + e.getMessage());
      displayHelp(cliOptions);
    }
    String localhost = InetAddress.getLocalHost().getHostName();
    int localPort = -1;
    try {
      localPort = Integer.parseInt(cmd.getOptionValue("p"));
    } catch (Exception e) {
      displayHelp(cliOptions);
    }
    // Process Manager case.
    if (cmd.hasOption("m") && !cmd.hasOption("c")) {
      // First attempt to start the server.
      ProcessManagerServer pms = new ProcessManagerServer(localPort);
      Thread pmsThread = new Thread(pms);
      pmsThread.start();
      // If succeed, bring up the shell for user to operate.
      ProcManagerShell pm = new ProcManagerShell(localhost, localPort);
      Thread pmThread = new Thread(pm);
      pmThread.start();
      return;
    }
    // Child Manager case.
    else if (cmd.hasOption("c") && !cmd.hasOption("m")) {
      // Start the child server.
      ChildManager pcs = new ChildManager(localPort);
      Thread pcsThread = new Thread(pcs);
      pcsThread.start();
      return;
    }
    displayHelp(cliOptions);
  }
}

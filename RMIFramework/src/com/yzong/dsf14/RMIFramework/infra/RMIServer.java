package com.yzong.dsf14.RMIFramework.infra;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class contains the entry point for an RMI Server. It maintains a Hashtable from Remote
 * Object References to the actual Remote Objects (RMI Servants), and the client applications can
 * access them by calling methods locally via the stub created by <tt>localise</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIServer {

  private volatile static RoREntryTable tbl = new RoREntryTable();

  /**
   * Displays help message to the user by using Apache Commons CLI Library.
   * 
   * @param ops Options list
   */
  static void displayHelp(Options ops) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("java -jar RMIServer.jar -h RegHostName -p RegPortNum", ops);
    System.exit(1);
  }

  /**
   * Entry point for RMI Server.
   * 
   * @param args Command-line arguments to instantiate the RMI Servant.
   */
  public static void main(String args[]) {
    /* Parse command-line arguments. Obtain constants. */
    String RegHostName = "";
    int RegPort = -1;
    String LocalHostName = "";
    int LocalPort = -1;
    Options cliOptions = new Options();
    cliOptions.addOption("h", true, "Hostname for RMI Registry");
    cliOptions.addOption("p", true, "Port for RMI Registry");
    CommandLineParser cliParser = new GnuParser();
    CommandLine cmd = null;
    try {
      cmd = cliParser.parse(cliOptions, args);
    } catch (Exception e) {
      System.err.println("Parsing failed. Reason: " + e.getMessage());
      displayHelp(cliOptions);
    }
    if (cmd.hasOption("p") && cmd.getOptionValue("p") != null
        && NumberUtils.isNumber(cmd.getOptionValue("p")) && cmd.hasOption("h")
        && cmd.getOptionValue("h") != null) {
      RegHostName = cmd.getOptionValue("h");
      RegPort = Integer.parseInt(cmd.getOptionValue("p"));
    } else {
      displayHelp(cliOptions);
    }
    /* Obtains Local Hostname. */
    try {
      LocalHostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      System.err.println("ERROR -- Cannot get local host name!");
      return;
    }
    /* Binds a free RMI Server port and start the RMI Server CLI. */
    ServerSocket srv = null;
    try {
      srv = new ServerSocket(0);
      LocalPort = srv.getLocalPort();
      System.out.printf("INFO -- RMI Server started at %s:%d. Master RMI Registry at %s:%d.\n",
          LocalHostName, LocalPort, RegHostName, RegPort);
      RMIServerCLI cli = new RMIServerCLI(RegHostName, RegPort, LocalHostName, LocalPort, tbl);
      Thread cliThread = new Thread(cli);
      cliThread.start();
      /* Mainloop dispatcher that handles client application method invocations. */
      while (true) {
        try {
          Socket srvSocket = srv.accept();
          ObjectOutputStream out = new ObjectOutputStream(srvSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(srvSocket.getInputStream());
          RMIInvocationPkg invoc = (RMIInvocationPkg) in.readObject(); // Client invocation package
          String inftName = invoc.getRoR().getRemoteInterfaceName(); // Remote Interface name
          String methodName = invoc.getMethodName(); // Method name
          Class<?> intfClass = Class.forName(inftName); // Object interface name
          Class<?>[] paramTypes = new Class<?>[invoc.getArgs().length]; // Params class list
          /* Find the actual remote object from its RoR (Pass by Reference) */
          Object[] argsList = invoc.getArgs();
          for (int i = 0; i < argsList.length; i++) {
            if (argsList[i] instanceof RemoteObjectRef) {
              argsList[i] = tbl.findObjByRoR((RemoteObjectRef) argsList[i]);
            }
            paramTypes[i] = argsList[i].getClass();
          }
          /* Invokes the Remote Method and return the result to the client application. */
          Method invokedMethod =
              intfClass.cast(tbl.findObjByRoR(invoc.getRoR())).getClass()
                  .getMethod(methodName, paramTypes);
          out.writeObject(invokedMethod.invoke(intfClass.cast(tbl.findObjByRoR(invoc.getRoR())),
              invoc.getArgs()));
          srvSocket.close();
        } catch (Exception e) {
          System.out.printf("ERROR -- %s\n", e.getMessage());
        }
      }
    } catch (Exception e) {
      System.out.printf("ERROR -- %s\n", e.getMessage());
    } finally {
      try {
        srv.close();
      } catch (IOException e) {
      }
      System.out.println("INFO -- Goodbye!");
    }
  }
}

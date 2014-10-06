package com.yzong.dsf14.RMIFramework.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.lang3.math.NumberUtils;

import com.yzong.dsf14.RMIFramework.examples.ZipCodeServer;

/**
 * This class contains the entry point for an RMI Servant. It maintains a Hashtable from Remote
 * Object References to the actual Remote Objects, and the client applications can access them by
 * calling functions locally via the stub created by <tt>localise</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIServant {

  private static String ServiceName;
  private static String ClassName;
  private static String RegHostName;
  private static int RegPort;
  private static String LocalHostName;
  private static int LocalPort;

  /**
   * Entry point for RMI Servant.
   * 
   * @param args Command-line arguments to instantiate the RMI Servant.
   */
  public static void main(String args[]) {
    /* Parse command-line arguments. */
    if (args.length != 5 || !NumberUtils.isNumber(args[2]) || !NumberUtils.isNumber(args[4])) {
      System.err
          .println("Usage:\tjava -jar RMIService.jar ClassName RegistryHostname RegistryPort ServiceName LocalPort");
      System.exit(1);
    }
    ClassName = args[0];
    RegHostName = args[1];
    RegPort = Integer.parseInt(args[2]);
    ServiceName = args[3];
    LocalPort = Integer.parseInt(args[4]);
    /* Obtains the Remote Implementation Class. */
    Class<?> remoteImpl;
    try {
      remoteImpl = Class.forName(ClassName);
    } catch (ClassNotFoundException e) {
      System.err.printf("ERROR -- Remote implementation class \"%s\" cannot be loaded!\n",
          ClassName);
      return;
    }
    /* Obtains Local Hostname. */
    try {
      LocalHostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      System.err.println("ERROR -- Cannot get local host name!");
      return;
    }
    /* Compiles Local Stub of the Remote Object. */
    if (remoteImpl.getInterfaces().length != 1) {
      System.err
          .println("ERROR -- Cannot determine the interface of the Remote Object (only one interface permitted)!");
      return;
    }
    Class<?> remoteIntf = remoteImpl.getInterfaces()[0];
    InvocationHandler handler = new RMIMethodHandler();
    remoteIntf.cast(Proxy.newProxyInstance(remoteIntf.getClassLoader(),
            new Class[] {remoteIntf}, handler)).getClass();
    /* Initializes RoR table with the Remote Object. */
    RoREntryTable tbl = new RoREntryTable();
    long objKey = tbl.addObj(LocalHostName, LocalPort, remoteImpl);
    /* Informs RMI Registry that the Service is ready. */
    RMIRegistryClient sr = LocateRMIRegistry.getRegistry(RegHostName, RegPort);
    if (sr == null) {
      System.err.printf("ERROR -- Cannot establish connection with RMI Registry at %s:%d!\n",
          RegHostName, RegPort);
      return;
    }
    sr.bind(ServiceName, LocalHostName, LocalPort, objKey, ClassName);
    /* Main loop: Accepts requests from client applications. */
    ServerSocket srvSocket = null;
    try {
      srvSocket = new ServerSocket(LocalPort);
      System.out.printf("INFO -- RMI Servant started at port %d.\n", LocalPort);
      while (true) {
        Socket clientSocket = srvSocket.accept();
        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        /* TODO: Respond to client requests: method invocation on RoR */
      }
    } catch (Exception e) {
      System.err.println("ERROR -- Network I/O error occured!\n");
      return;
    } finally {
      try {
        srvSocket.close();
      } catch (IOException e) {
      }
      System.out.println("INFO -- Goodbye!");
    }
  }

}

package com.yzong.dsf14.RMIFramework.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Runnable class for RMI Registry Server. It handles various client commands issued by
 * <tt>RMIRegistryClient</tt> to interact with the Registry.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIRegistryServer implements Runnable {

  private String localHost;
  private int localPort;
  private Hashtable<String, RemoteObjectRef> RoRTable;

  public RMIRegistryServer(String host, int port) {
    this.localHost = host;
    this.localPort = port;
    this.RoRTable = new Hashtable<String, RemoteObjectRef>();
  }

  /**
   * Main loop for RMI Registry Server. It handles the following commands:
   * <ul>
   * <li><tt>ping</tt>: When a client tests if the Registry is alive. Always reply <tt>PONG</tt>;</li>
   * <li><tt>list</tt>: When a client queries for all Services. Reply
   * <tt>["OK", number of SrvNames, Srv
   * Names...]</tt>;</li>
   * <li><tt>unbind</tt>: When a client unbinds a Service Name. Reply
   * <tt>[{"OK", "ERROR"}, [message]]</tt>;</li>
   * <li>
   * <tt>bind<tt>: When a client wants to register an (inexistent) Service Name. Reply <tt>[{"OK", "ERROR"},
   * [message]]</tt>;</li>
   * <li><tt>rebind</tt>: When a client wants to <i>force</i> register a Service Name. Reply
   * <tt>[{"OK", "ERROR"},
   * [message]]</tt>;</li>
   * <li><tt>lookup</tt>: When a client wants to query for a specific Service Name. Reply
   * <tt>[{"FOUND",
   * "NOTFOUND", "ERROR"}, {[message], [IP, Port, ObjKey, RemoteInterfaceName]}]</tt>;</li>
   * </ul>
   */
  @Override
  public void run() {
    System.out.printf("INFO -- Starting RMI Registry Server at %s:%d...\n", localHost, localPort);
    ServerSocket serverSocket = null;
    /* Mainloop for accepting requests from clients. */
    try {
      serverSocket = new ServerSocket(localPort);
      System.out.println("INFO -- RMI Registry Server started!");
      while (true) {
        Socket clientSocket = serverSocket.accept();
        BufferedReader in =
            new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String command = in.readLine();
        if (command == null) { // Client sent no command.
          out.println("ERROR");
          out.println("No command found!");
          System.out.println("WARNING -- Empty request received from client!");
          continue;
        } else if (command.equals("PING")) {
          out.println("PONG");
          System.out.println("INFO -- Processed PING request from client.");
        } else if (command.equals("LIST")) {
          out.println("OK");
          out.println(RoRTable.size());
          for (String srvName : RoRTable.keySet()) {
            out.println(srvName);
          }
          System.out.println("INFO -- Processed LIST request from client.");
        } else if (command.equals("UNBIND")) {
          String srvName = in.readLine();
          if (srvName == null) {
            out.println("ERROR");
            out.println("Service Name not given!");
            System.out
                .println("ERROR -- UNBIND request from client does not contain Service Name.");
          } else if (!RoRTable.containsKey(srvName)) {
            out.println("ERROR");
            out.println("Service Name not found!");
            System.out.println("ERROR -- UNBIND request from client has inexistent Service Name.");
          } else {
            RoRTable.remove(srvName);
            out.println("OK");
            System.out.printf("INFO -- Successfully UNBIND service %s.\n", srvName);
          }
        } else if (command.equals("BIND") || command.equals("REBIND")) {
          String srvName = in.readLine();
          String IPAddr = in.readLine();
          String port = in.readLine();
          String objKey = in.readLine();
          String remoteInterfaceName = in.readLine();
          /* Check for insufficient or invalid arguments. */
          if (srvName == null || IPAddr == null || port == null || objKey == null
              || remoteInterfaceName == null || !NumberUtils.isNumber(port)
              || !NumberUtils.isNumber(objKey)) {
            out.println("ERROR");
            out.printf("Invalid arguments for %s!\n", command);
            System.out.printf("ERROR -- %s request contains invalid arguments!\n", command);
          }
          /* Check if the Service Name already exists. */
          else if (command.equals("BIND") && RoRTable.containsKey(srvName)) {
            out.println("ERROR");
            out.printf("Service Name \"%s\" already exists!\n", srvName);
            System.out.printf("ERROR -- BIND request issued for existing Service Name \"%s\"!\n",
                srvName);
          }
          /* Otherwise, update the Registry with the desired (srvName, RoR) pair. */
          else {
            RemoteObjectRef ror =
                new RemoteObjectRef(IPAddr, Integer.parseInt(port), Long.parseLong(objKey),
                    remoteInterfaceName);
            RoRTable.remove(srvName);
            RoRTable.put(srvName, ror);
            out.println("OK");
            System.out.printf("INFO -- Successfully %s \"%s\" on Registry.\n", command, srvName);
          }
        } else if (command.equals("LOOKUP")) {
          String srvName = in.readLine();
          if (srvName == null) {
            out.println("ERROR");
            out.println("Service Name not given!");
            System.out
                .println("ERROR -- LOOKUP request from client does not contain Service Name.");
          } else if (!RoRTable.containsKey(srvName)) {
            out.println("NOTFOUND");
            out.println("Service Name not found!");
            System.out.println("WARNING -- LOOKUP request asks for inexistent Service Name.");
          } else {
            RemoteObjectRef ror = RoRTable.get(srvName);
            out.println("FOUND");
            out.println(ror.getHostName());
            out.println(ror.getPort());
            out.println(ror.getObjKey());
            out.println(ror.getRemoteInterfaceName());
            System.out.printf("INFO -- Successfully LOOKUP Service Name \"%s\":{%s, %d, %s, %s}\n",
                srvName, ror.getHostName(), ror.getPort(), ror.getObjKey(),
                ror.getRemoteInterfaceName());
          }
        }
        /* Make sure we flush everything out before closing the current session. */
        out.flush();
        clientSocket.close();
      }
    } catch (Exception e) {
      System.err.printf("ERROR -- Failed binding port %d!\n", localPort);
      e.printStackTrace();
    } finally {
      System.out.println("INFO -- Goodbye!");
    }
  }
}

package com.yzong.dsf14.RMIFramework.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to interact with an RMI Registry Server from a client's perspective.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIRegistryClient {

  private String HostName;
  private int Port;

  public String getHostName() {
    return HostName;
  }

  public int getPort() {
    return Port;
  }

  public RMIRegistryClient(String hostName, int port) {
    this.HostName = hostName;
    this.Port = port;
  }

  /**
   * Sends a ping signal to RMI Register and waits for reply.
   * 
   * @return <tt>true</tt> if and only if the Registry server is up.
   */
  public synchronized boolean ping() {
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("PING");
      String response = in.readLine();
      clientSocket.close();
      return (response.equals("PONG"));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Requests a list of Service Names that are registered on RMI Registery.
   * 
   * @return List of registered Service Names on Registry.
   */
  public synchronized List<String> list() {
    List<String> srvNames = new ArrayList<String>();
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("LIST");
      String response = in.readLine();
      if (!response.equals("OK")) {
        clientSocket.close();
        return srvNames;
      }
      int srvNameCount = Integer.parseInt(in.readLine());
      for (int i = 0; i < srvNameCount; i++) {
        String srvName = in.readLine();
        if (srvName != null) {
          srvNames.add(srvName);
        } else {
          break;
        }
      }
      clientSocket.close();
      return srvNames;
    } catch (Exception e) {
      return srvNames;
    }
  }

  /**
   * Unbinds the given Service Name from the RMI Registry Server.
   * 
   * @param serviceName Service Name to be unbound
   * @return Empty string if succeeded, otherwise the error message.
   */
  public synchronized String unbind(String serviceName) {
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("UNBIND");
      out.println(serviceName);
      String response = in.readLine();
      if (!response.equals("OK")) {
        String errorMsg = in.readLine();
        clientSocket.close();
        return errorMsg;
      }
      clientSocket.close();
      return "";
    } catch (Exception e) {
      return String.format("Network error while unbinding %s!", serviceName);
    }
  }

  /**
   * Binds a service along with its properties to RMI Registry. Reports an error if the given
   * Service Name already exists in the Registry.
   * 
   * @param serviceName Service Name of the object in RMI Registry
   * @param hostName Host name of the RMI Servant
   * @param port Port number of the RMI Servant
   * @param objectKey Object Key of the RMI Object
   * @param remoteInterfaceName Remote Interfact Name of the RMI Object
   * @return Empty string if succeeded, otherwise the error message.
   */
  public synchronized String bind(String serviceName, String hostName, int port, long objectKey,
      String remoteInterfaceName) {
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("BIND");
      out.println(serviceName);
      out.println(hostName);
      out.println(port);
      out.println(objectKey);
      out.println(remoteInterfaceName);
      String response = in.readLine();
      if (!response.equals("OK")) {
        String errorMsg = in.readLine();
        clientSocket.close();
        return errorMsg;
      }
      clientSocket.close();
      return "";
    } catch (Exception e) {
      e.printStackTrace();
      return String.format("Network error while binding %s!", serviceName);
    }
  }

  /**
   * Binds a service along with its properties to RMI Registry. Overwrites the original entry if the
   * given Service Name already exists in the Registry.
   * 
   * @param serviceName Service Name of the object in RMI Registry
   * @param hostName Host name of the RMI Servant
   * @param port Port number of the RMI Servant
   * @param objectKey Object Key of the RMI Object
   * @param remoteInterfaceName Remote Interfact Name of the RMI Object
   * @return Empty string if succeeded, otherwise the error message.
   */
  public synchronized String rebind(String serviceName, String hostName, int port, long objectKey,
      String remoteInterfaceName) {
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("REBIND");
      out.println(serviceName);
      out.println(hostName);
      out.println(port);
      out.println(objectKey);
      out.println(remoteInterfaceName);
      String response = in.readLine();
      if (!response.equals("OK")) {
        String errorMsg = in.readLine();
        clientSocket.close();
        return errorMsg;
      }
      clientSocket.close();
      return "";
    } catch (Exception e) {
      return String.format("Network error while unbinding %s!", serviceName);
    }
  }

  /**
   * Looks up the RMI Registry for the Remote Object Reference with the given Service Name.
   * 
   * @param serviceName Service Name to look up
   * @return The corresponding Remote Object Reference if the Service Name exists; otherwise
   *         <tt>null</tt>.
   */
  public synchronized RemoteObjectRef lookup(String serviceName) {
    try {
      Socket clientSocket = new Socket(HostName, Port);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      out.println("LOOKUP");
      out.println(serviceName);
      String response = in.readLine();
      if (!response.equals("FOUND")) {
        clientSocket.close();
        return null;
      }
      String hostName = in.readLine();
      String port = in.readLine();
      String objKey = in.readLine();
      String remoteInterfaceName = in.readLine();
      clientSocket.close();
      return new RemoteObjectRef(hostName, Integer.parseInt(port), Long.parseLong(objKey),
          remoteInterfaceName);
    } catch (Exception e) {
      return null;
    }
  }

}

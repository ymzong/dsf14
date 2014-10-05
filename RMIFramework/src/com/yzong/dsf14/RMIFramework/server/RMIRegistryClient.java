package com.yzong.dsf14.RMIFramework.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
  public boolean ping() {
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
  public List<String> list() {
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
  public String unbind(String serviceName) {
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
   * Binds a service along with its properties to RMI Registry. Returns an error if the given
   * Service Name already exists in the Registry.
   * 
   * @param serviceName
   * @param hostName
   * @param port
   * @param objectKey
   * @param remoteInterfaceName
   * @return Empty string if succeeded, otherwise the error message.
   */
  public String bind(String serviceName, String hostName, int port, String objectKey,
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
   * @param serviceName
   * @param hostName
   * @param port
   * @param objectKey
   * @param remoteInterfaceName
   * @return Empty string if succeeded, otherwise the error message.
   */
  public String rebind(String serviceName, String hostName, int port, String objectKey,
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
   * @return The Remote Object Reference if the Service Name exists; otherwise <tt>null</tt>.
   */
  public RemoteObjectRef lookup(String serviceName) {
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
      return new RemoteObjectRef(hostName, Integer.parseInt(port), objKey, remoteInterfaceName);
    } catch (Exception e) {
      return null;
    }
  }

}

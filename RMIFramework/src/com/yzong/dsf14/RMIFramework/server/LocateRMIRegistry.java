package com.yzong.dsf14.RMIFramework.server;

public class LocateRMIRegistry {

  /**
   * Given hostname and port, detects if the endpoint is a RMI Registry. If so, return the
   * corresponding RMI Registry Client object; otherwise return <tt>null</tt>.
   * 
   * @param hostName Host name for the remote endpoint
   * @param port Port number for the remote endpoint
   * @return RMI Registry Client object if endpoint is valid; otherwise <tt>null</tt>
   */
  public static RMIRegistryClient getRegistry(String hostName, int port) {
    RMIRegistryClient rmiClient = new RMIRegistryClient(hostName, port);
    if (rmiClient.ping()) {
      return rmiClient;
    } else {
      return null;
    }
  }

}

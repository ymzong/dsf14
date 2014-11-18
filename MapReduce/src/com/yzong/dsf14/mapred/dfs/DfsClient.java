/**
 * 
 */
package com.yzong.dsf14.mapred.dfs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client object with methods to interact with the DFS.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class DfsClient {

  private String MasterHost;
  private int MasterPort;
  public DfsConfig Config;
  private boolean IsMaster;

  public DfsClient(String host, int port) {
    MasterHost = host;
    MasterPort = port;
    IsMaster = false;
  }

  public DfsClient(String host, int port, boolean isMaster) {
    MasterHost = host;
    MasterPort = port;
    IsMaster = isMaster;
  }

  /**
   * @return the masterHost
   */
  public String getMasterHost() {
    return MasterHost;
  }

  /**
   * @return the masterPort
   */
  public int getMasterPort() {
    return MasterPort;
  }

  /**
   * Request master to dump a local copy of a file in DFS to some path.
   * 
   * @param fileName Filename of the file in DFS.
   * @param localPath Target path to store the file in local file system.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean getFile(String fileName, String localPath) {
    /* The request must come from a master node. */
    if (!IsMaster) {
      System.err.println("ERROR -- Only master node is allowed to pull file from DFS!");
      return false;
    }
    try {
      Socket clientSocket = new Socket(MasterHost, MasterPort);
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      DfsRequestPkg outPkg = new DfsRequestPkg("DUMP", new Object[] {fileName, localPath});
      out.writeObject(outPkg);
      DfsRequestPkg inPkg = (DfsRequestPkg) in.readObject();
      clientSocket.close();
      if (inPkg.Command.equals("OK")) {
        System.out.printf("File `%s` dumped at `%s` successfully!\n", fileName, localPath);
        return true;
      } else {
        System.out.printf("Error while dumping `%s` -- %s", fileName, (String) inPkg.Body);
        return false;
      }
    } catch (Exception e) {
      System.out.printf("Exception while dumping `%s` -- %s", fileName, e.getMessage());
      return false;
    }
  }

}

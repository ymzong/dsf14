package com.yzong.dsf14.mapred.dfs;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.io.FileUtils;

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
   * Request master to add a local copy of a file into DFS.
   * 
   * @param localPath Local path of the file to be stored in DFS.
   * @param fileName Filename of the file in DFS.
   * @return <tt>true</tt> iff the operation succeeds.
   */
  public boolean putFile(String localPath, String fileName) {
    try {
      Socket clientSocket = new Socket(MasterHost, MasterPort);
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      DfsRequestPkg outPkg =
          new DfsRequestPkg("PUT", new Object[] {fileName,
              FileUtils.readFileToByteArray(new File(localPath))});
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

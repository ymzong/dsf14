package com.yzong.dsf14.mapred.runnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.io.FileUtils;

import com.yzong.dsf14.mapred.framework.MapRedWorkerStatus;
import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ClusterStatus;
import com.yzong.dsf14.mapred.util.MapRedMessage;

/**
 * I/O Handler for MapRed Worker server. Take in an object from <tt>ObjectInputStream</tt>, then
 * return a response to <tt>ObjectOutputStream</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedWorkerController implements Runnable {

  private ClusterConfig CC;
  private MapRedWorkerStatus WS;
  private ObjectInputStream InStream;
  private ObjectOutputStream OutStream;

  public MapRedWorkerController(ClusterConfig cc, MapRedWorkerStatus ws, ObjectInputStream in,
      ObjectOutputStream out) {
    InStream = in;
    OutStream = out;
    CC = cc;
    WS = ws;
  }

  @Override
  synchronized public void run() {
    try {
      com.yzong.dsf14.mapred.util.MapRedMessage inPkg = (MapRedMessage) InStream.readObject();
      /* Case One: Heartbeat request. */
      if (inPkg.Command.equals("PING")) {
        System.out.println("INFO -- `PING` request received from client.");
        OutStream.writeObject(new MapRedMessage("PONG", null));
      }
      /* Case Two: Master pushes shard request. */
      else if (inPkg.Command.equals("DFS/ADD")) {
        System.out.println("INFO -- `ADD` request received from client.");
        MapRedMessage outPkg = null;
        try {
          Object[] bodyArgs = (Object[]) inPkg.Body;
          StringBuffer text = (StringBuffer) bodyArgs[0];
          String fileName = (String) bodyArgs[1];
          String outPath = WS.WorkingDir + "/" + fileName;
          BufferedWriter outBuffer = new BufferedWriter(new FileWriter(outPath));
          outBuffer.write(text.toString());
          outBuffer.flush();
          outBuffer.close();
          outPkg = new com.yzong.dsf14.mapred.util.MapRedMessage("OK", outPath);
        } catch (Exception e) {
          outPkg = new com.yzong.dsf14.mapred.util.MapRedMessage("XXX", e.getMessage());
          System.out.printf("ERROR -- %s happened while processing `ADD`.\n", e.getMessage());
        }
        OutStream.writeObject(outPkg);
      }
      /* Case Three: Client requests file shard. */
      else if (inPkg.Command.equals("DFS/GET")) {
        System.out.println("INFO -- `GET` request received from client.");
        MapRedMessage outPkg = null;
        try {
          String localPath = (String) inPkg.Body;
          outPkg = new MapRedMessage("OK", FileUtils.readFileToString(new File(localPath)));
        } catch (Exception e) {
          outPkg = new MapRedMessage("XXX", e.getMessage());
          System.out.printf("ERROR -- %s happened while processing `GET`.\n", e.getMessage());
        }
        OutStream.writeObject(outPkg);
      }
      /* Case Four: Master terminates DFS sessoin. */
      else if (inPkg.Command.equals("ALL/DESTROY")) {
        /* Remove local tmp directory */
        System.out.println("INFO -- `DESTROY` request received from Master.");
        OutStream.writeObject(new MapRedMessage("OK", null));
        System.out.println("INFO -- Cleaning up working directory...");
        FileUtils.deleteDirectory(new File(WS.WorkingDir));
        return;
      }
      /* Case Five: Incoming package cannot be interpreted. */
      else {
        System.out.println("ERROR -- Incoming package cannot be interpreted!");
        OutStream.writeObject(new MapRedMessage("XXX", null));
      }
    } catch (Exception e) {
      System.out.printf("ERROR -- Exception caught while processing request: %s\n", e.getMessage());
    }
  }
}

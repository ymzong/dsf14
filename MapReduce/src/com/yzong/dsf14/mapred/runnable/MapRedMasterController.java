package com.yzong.dsf14.mapred.runnable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.yzong.dsf14.mapred.util.ClusterConfig;
import com.yzong.dsf14.mapred.util.ClusterStatus;

/**
 * I/O Handler for MapRed Master server. Take in an object from <tt>ObjectInputStream</tt>, then
 * return a response to <tt>ObjectOutputStream</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedMasterController implements Runnable {

  private ClusterConfig CC;
  private ClusterStatus CS;
  private ObjectInputStream InStream;
  private ObjectOutputStream OutStream;

  public MapRedMasterController(ClusterConfig cc, ClusterStatus cs, ObjectInputStream in,
      ObjectOutputStream out) {
    CC = cc;
    CS = cs;
    InStream = in;
    OutStream = out;
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

  }

}

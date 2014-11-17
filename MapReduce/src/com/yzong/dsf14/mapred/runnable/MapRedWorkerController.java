package com.yzong.dsf14.mapred.runnable;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * I/O Handler for MapRed Worker server. Take in an object from <tt>ObjectInputStream</tt>, then
 * return a response to <tt>ObjectOutputStream</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedWorkerController implements Runnable {

  private ObjectInputStream InStream;
  private ObjectOutputStream OutStream;

  public MapRedWorkerController(ObjectInputStream in, ObjectOutputStream out) {
    InStream = in;
    OutStream = out;
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

  }

}

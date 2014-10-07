package com.yzong.dsf14.RMIFramework.examples;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIInvocationPkg;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

/**
 * A manually created RMI Stub for <tt>ZipCodeServer</tt> interface.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ZipCodeServer_stub implements ZipCodeServer {

  public RemoteObjectRef selfRoR;

  public void setSelfRoR(RemoteObjectRef selfRoR) {
    this.selfRoR = selfRoR;
  }

  @Override
  public void initialise(ZipCodeList newlist) throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("initialise", new Object[] {newlist}, selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      in.readObject(); // Function has no return value -- read an Object for ACK.
      clientSocket.close();
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }

  @Override
  public String find(String city) throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("find", new Object[] {city}, selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      String result = (String) in.readObject();
      clientSocket.close();
      return result;
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }

  @Override
  public ZipCodeList findAll() throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("findAll", new Object[] {}, selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      ZipCodeList result = (ZipCodeList) in.readObject();
      clientSocket.close();
      return result;
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }

  @Override
  public void printAll() throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("printAll", new Object[] {}, selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      in.readObject(); // Function has no return value -- read an Object for ACK.
      clientSocket.close();
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }

}

package com.yzong.dsf14.RMIFramework.examples;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIInvocationPkg;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

public class CalculatorServer_stub implements CalculatorServer {

  public RemoteObjectRef selfRoR;

  public void setSelfRoR(RemoteObjectRef selfRoR) {
    this.selfRoR = selfRoR;
  }

  @Override
  public void initialize(String name) throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("initialize", new Object[] {name}, selfRoR);
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
  public int add(Integer x, Integer y) throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("add", new Object[] {x, y}, selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      int result = (int) in.readObject();
      clientSocket.close();
      return result;
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }

  @Override
  public String identify() throws RMIInvocationException {
    RMIInvocationPkg pkg = new RMIInvocationPkg("identify", new Object[] {}, selfRoR);
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

  public int secretMethod(ZipCodeServer srv, Integer x) throws RMIInvocationException {
    /* We are passing the ZipCodeServer as Reference! */
    RMIInvocationPkg pkg =
        new RMIInvocationPkg("secretMethod", new Object[] {((ZipCodeServer_stub) srv).selfRoR, x},
            selfRoR);
    try {
      Socket clientSocket = new Socket(selfRoR.getHostName(), selfRoR.getPort());
      ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
      out.writeObject(pkg);
      int result = (int) in.readObject();
      clientSocket.close();
      return result;
    } catch (Exception e) {
      throw new RMIInvocationException();
    }
  }
}

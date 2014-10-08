package com.yzong.dsf14.RMIFramework.examples;

import java.io.*;

import com.yzong.dsf14.RMIFramework.infra.LocateRMIRegistry;
import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIRegistryClient;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

/**
 * This class contains a <tt>main</tt> method that acts as a client of the <tt>ZipCodeServer</tt>.
 * It instantiates a local stub of <tt>ZipCodeServer</tt> and calls its methods via the RMI
 * framework, as if the object were local.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ZipCodeClient {

  /**
   * Main test routine for <tt>ZipCodeServer</tt>.
   * 
   * @param args None necessary
   * @throws IOException
   * @throws RMIInvocationException
   */
  public static void main(String[] args) throws IOException, RMIInvocationException {
    /* Loads user configurations. */
    Console console = System.console();
    String host = console.readLine("Registry Hostname: ");
    int port = Integer.parseInt(console.readLine("Registry Port Number: "));
    String serviceName = console.readLine("Registry Service Name for ZipCodeServer: ");
    String fileName = console.readLine("Data File Path: ");

    /* Locates the RMI Registry and gets the Remote Object Reference with the given Service Name. */
    RMIRegistryClient sr = LocateRMIRegistry.getRegistry(host, port);
    RemoteObjectRef ror = sr.lookup(serviceName);

    /* Obtains the RMI Stub from Remote Object Reference. */
    ZipCodeServer zcs = (ZipCodeServer) ror.localize();

    /* Construct a local zip code list from the input data file. */
    ZipCodeList l = null;
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    boolean flag = true;
    while (flag) {
      String city = in.readLine();
      String code = in.readLine();
      if (city == null)
        flag = false;
      else
        l = new ZipCodeList(city.trim(), code.trim(), l);
    }
    in.close();

    /* Prints out the local ZipCodeList. */
    System.out.println("This is the original list:");
    ZipCodeList temp = l;
    while (temp != null) {
      System.out.println("city: " + temp.city + ", " + "code: " + temp.ZipCode);
      temp = temp.next;
    }

    /* Tests `initialize` method. */
    zcs.initialise(l);
    System.out.println("\nServer initalised.");

    /* Tests `find` method. */
    System.out.println("\nThis is the remote list given by `find`.");
    temp = l;
    while (temp != null) {
      String res = zcs.find(temp.city);
      System.out.println("city: " + temp.city + ", " + "code: " + res);
      temp = temp.next;
    }

    /* Tests `findAll` method. */
    System.out.println("\nThis is the remote list given by `findall`.");
    temp = zcs.findAll();
    while (temp != null) {
      System.out.println("city: " + temp.city + ", " + "code: " + temp.ZipCode);
      temp = temp.next;
    }

    /* Tests `printAll` method. */
    System.out.println("\nWe test the remote site printing.");
    zcs.printAll();

    return;
  }
}

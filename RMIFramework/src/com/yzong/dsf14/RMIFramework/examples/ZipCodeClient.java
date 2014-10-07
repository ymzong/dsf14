package com.yzong.dsf14.RMIFramework.examples;

// a client for ZipCodeServer.
// it uses ZipCodeServer as an interface, and test
// all methods.

// It reads data from a file containing the service name and city-zip
// pairs in the following way:
// city1
// zip1
// ...
// ...
// end.

import java.io.*;

import com.yzong.dsf14.RMIFramework.server.LocateRMIRegistry;
import com.yzong.dsf14.RMIFramework.server.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.server.RMIRegistryClient;
import com.yzong.dsf14.RMIFramework.server.RemoteObjectRef;

/**
 * This class contains a <tt>main</tt> method that acts as a client of the <tt>ZipCodeServer</tt>.
 * It instantiate a local stub of <tt>ZipCodeServer</tt> and calls its methods via the RMI
 * framework, as if the object were local.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ZipCodeClient {

  /**
   * Main test routine for <tt>ZipCodeServer</tt>.
   * 
   * @param args [Registry Hostname, Registry Port Number, Service Name, Data File Name]
   * @throws IOException
   * @throws RMIInvocationException 
   */
  public static void main(String[] args) throws IOException, RMIInvocationException {
    /* Parses command-line arguments */
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String serviceName = args[2];
    BufferedReader in = new BufferedReader(new FileReader(args[3]));

    /* Locates the RMI Registry and gets the Remote Object Reference with the given Service Name. */
    RMIRegistryClient sr = LocateRMIRegistry.getRegistry(host, port);
    RemoteObjectRef ror = sr.lookup(serviceName);

    /* Obtains the RMI Stub from Remote Object Reference. */
    ZipCodeServer zcs = (ZipCodeServer) ror.localize();

    /* Construct a local zip code list from the input data file. */
    ZipCodeList l = null;
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
  }
}

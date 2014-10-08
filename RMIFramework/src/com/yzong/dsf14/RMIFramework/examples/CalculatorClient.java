package com.yzong.dsf14.RMIFramework.examples;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;

import com.yzong.dsf14.RMIFramework.infra.LocateRMIRegistry;
import com.yzong.dsf14.RMIFramework.infra.RMIInvocationException;
import com.yzong.dsf14.RMIFramework.infra.RMIRegistryClient;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

/**
 * This class contains a <tt>main</tt> method that acts as a client of the <tt>CalculatorServer</tt>
 * . It instantiates a local stub of <tt>CalculatorServer</tt> and calls its methods via the RMI
 * framework, as if the object were local.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class CalculatorClient {

  /**
   * Main test routine for <tt>CalculatorServer</tt>.
   * 
   * @param args None
   * @throws IOException
   * @throws RMIInvocationException
   */
  public static void main(String[] args) throws IOException, RMIInvocationException {
    /* Loads user configurations */
    Console console = System.console();
    String host = console.readLine("Registry Hostname: ");
    int port = Integer.parseInt(console.readLine("Registry Port Number: "));
    String serviceName = console.readLine("Registry Service Name for CalculatorServer: ");

    /* Locates the RMI Registry and gets the Remote Object Reference with the given Service Name. */
    RMIRegistryClient sr = LocateRMIRegistry.getRegistry(host, port);
    RemoteObjectRef calcRoR = sr.lookup(serviceName);

    /* Obtains the RMI Stub from Remote Object Reference. */
    CalculatorServer calcServ = (CalculatorServer) calcRoR.localize();

    /* Carries out the test routines for Calculator Object. */
    String calcName = "CalcFooBar";
    System.out.printf("Setting the name of Calculator Object as: %s\n", calcName);
    calcServ.initialize(calcName);
    System.out.printf("The remote Calculator Object now has name: %s\n", calcServ.identify());
    System.out.printf("The remote Calculator Object says: 15 + 440 = %d.\n",
        calcServ.add(new Integer(15), new Integer(440)));

    /* Obtains a new ZipCode RoR. */
    serviceName = console.readLine("Registry Service Name for ZipCodeServer: ");
    String dataFile = console.readLine("Data File Path: ");
    RemoteObjectRef zipCodeRoR = sr.lookup(serviceName);
    ZipCodeServer zipCodeSrv = (ZipCodeServer) zipCodeRoR.localize();

    /* Construct a local zip code list from the input data file. */
    System.out.println("Initializing the ZipCodeSever...");
    BufferedReader in = new BufferedReader(new FileReader(dataFile));
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
    zipCodeSrv.initialise(l);

    /* Tests the SecretMethod of CalculatorServer. */
    System.out.printf("There are %d entries in ZipCodeServ.\n",
        calcServ.secretMethod(zipCodeSrv, new Integer(0)));
    System.out.printf("That plus 100 would be %d. (Calculated on server!)\n",
        calcServ.secretMethod(zipCodeSrv, new Integer(100)));
    
    System.out.println("Goodbye!");
    return;
  }

}

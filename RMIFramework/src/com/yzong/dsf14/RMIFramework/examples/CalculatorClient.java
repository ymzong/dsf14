package com.yzong.dsf14.RMIFramework.examples;

import java.io.BufferedReader;
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
   * @param args [Registry Hostname, Registry Port Number, Service Name]
   * @throws IOException
   * @throws RMIInvocationException
   */
  public static void main(String[] args) throws IOException, RMIInvocationException {
    /* Parses command-line arguments */
    String host = args[0];
    int port = Integer.parseInt(args[1]);
    String serviceName = args[2];

    /* Locates the RMI Registry and gets the Remote Object Reference with the given Service Name. */
    RMIRegistryClient sr = LocateRMIRegistry.getRegistry(host, port);
    RemoteObjectRef ror = sr.lookup(serviceName);

    /* Obtains the RMI Stub from Remote Object Reference. */
    CalculatorServer calcServ = (CalculatorServer) ror.localize();
    
    /* TODO: Carry out the test routines. */
    return;
  }

}

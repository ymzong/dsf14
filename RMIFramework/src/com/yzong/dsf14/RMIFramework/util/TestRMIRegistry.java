package com.yzong.dsf14.RMIFramework.util;

import java.io.Console;
import java.util.List;

import com.yzong.dsf14.RMIFramework.infra.RMIRegistryClient;
import com.yzong.dsf14.RMIFramework.infra.RemoteObjectRef;

/**
 * This class contains a <tt>main</tt> method that tests all functionalities of RMI Registry. Be
 * sure to pass <tt>-ea</tt> flag to JVM to enable the <tt>assert</tt> statements.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class TestRMIRegistry {

  /**
   * Main test routine for RMI Registry.
   * 
   * @param args None
   */
  public static void main(String[] args) {
    System.out.println("Connecting to RMI Registery Server...");
    Console console = System.console();
    String hostName = console.readLine("Registry Hostname: ");
    String portNum = console.readLine("Registry Port Number: ");
    RMIRegistryClient rmic = new RMIRegistryClient(hostName, Integer.parseInt(portNum));
    System.out.println("Testing PING...");
    assert (rmic.ping());

    System.out.println("Testing BIND...");
    assert (rmic.bind("foobarService", "dummy.host", 10000, 0xc0ffee, "foobarServiceIntf")
        .equals(""));
    assert (!rmic.bind("foobarService", "dummy.host", 10000, 0xc0ffee, "foobarServiceIntf").equals(
        ""));
    assert (rmic.bind("foooooService", "dummy.host.1", 10001, 0x0000beef, "foooooServiceIntf")
        .equals(""));

    System.out.println("Testing LOOKUP");
    RemoteObjectRef ror = rmic.lookup("foobarService");
    assert (ror.getHostName().equals("dummy.host"));
    assert (ror.getObjKey() == 0xc0ffee);
    assert (ror.getPort() == 10000);
    assert (ror.getRemoteInterfaceName().equals("foobarServiceIntf"));
    ror = rmic.lookup("foooooService");
    assert (ror.getHostName().equals("dummy.host.1"));
    assert (ror.getObjKey() == 0x0000beef);
    assert (ror.getPort() == 10001);
    assert (ror.getRemoteInterfaceName().equals("foooooServiceIntf"));

    System.out.println("Testing REBIND");
    assert (rmic.bind("foobarService", "modified.host", 12345, 0x0000FFFF,
        "foobarServiceIntfModified").equals("Service Name \"foobarService\" already exists!"));
    assert (rmic.rebind("foobarService", "modified.host", 12345, 0x0000DDDD,
        "foobarServiceIntfModified").equals(""));
    ror = rmic.lookup("foobarService");
    assert (ror.getHostName().equals("modified.host"));
    assert (ror.getObjKey() == 0x0000DDDD);
    assert (ror.getPort() == 12345);
    assert (ror.getRemoteInterfaceName().equals("foobarServiceIntfModified"));

    System.out.println("Testing LIST");
    List<String> rorList = rmic.list();
    assert (rorList.size() == 2);
    assert (rorList.contains("foobarService"));
    assert (rorList.contains("foooooService"));

    System.out.println("Testing UNBIND");
    assert (rmic.lookup("foobarService") != null);
    assert (rmic.unbind("foobarService").equals(""));
    assert (rmic.lookup("foobarService") == null);
    assert (rmic.list().size() == 1);
    assert (rorList.contains("foooooService"));
    assert (rmic.unbind("foooooService").equals(""));
    assert (rmic.list().size() == 0);

    System.out.println("All tests passed!");
    return;
  }

}

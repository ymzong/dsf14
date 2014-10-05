package com.yzong.dsf14.RMIFramework.client;

import java.util.List;

import com.yzong.dsf14.RMIFramework.server.RMIRegistryClient;
import com.yzong.dsf14.RMIFramework.server.RemoteObjectRef;

public class TestRMIRegistry {

  private static final String RegistryHost = "localhost";
  private static final int RegistryPort = 9019;

  public static void main(String[] args) {
    System.out.println("Connecting to RMI Registery Server...");
    RMIRegistryClient rmic = new RMIRegistryClient(RegistryHost, RegistryPort);
    System.out.println("Testing PING...");
    assert(rmic.ping());

    System.out.println("Testing BIND...");
    assert (rmic.bind("foobarService", "dummy.host", 10000, 0xc0ffee, "foobarServiceIntf")
        .equals(""));
    assert (!rmic.bind("foobarService", "dummy.host", 10000, 0xc0ffee, "foobarServiceIntf")
        .equals(""));
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

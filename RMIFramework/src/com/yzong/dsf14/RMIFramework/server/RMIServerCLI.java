package com.yzong.dsf14.RMIFramework.server;

import java.io.Console;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class contains the Runnable for the RMI Registry Command-Line Interface. It is invoked by
 * the RMI Server main routine.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class RMIServerCLI implements Runnable {

  private String RegHostName;
  private int RegPort;
  private String LocalHostName;
  private int LocalPort;
  private volatile RoREntryTable RoRTable;
  private RMIRegistryClient RMIRegClient;

  public RMIServerCLI(String regHostName, int regPort, String localHostName, int localPort,
      RoREntryTable tbl) {
    this.RegHostName = regHostName;
    this.RegPort = regPort;
    this.LocalHostName = localHostName;
    this.LocalPort = localPort;
    this.RoRTable = tbl;
    this.RMIRegClient = LocateRMIRegistry.getRegistry(RegHostName, RegPort);
    if (this.RMIRegClient == null) {
      System.err.printf("ERROR -- Cannot contact the RMI Registry at %s:%d!\n", RegHostName,
          RegPort);
      System.exit(1);
    }
    System.out.println("INFO -- Connection to RMI Server established. CLI started.");
  }

  /**
   * Starts the mainloop for the RMI Registry CLI, where user can register/deregister services on
   * the RMI Server.
   */
  @Override
  public void run() {
    /* Mainloop for RMI Registry CLI. */
    Console console = System.console();
    String input = console.readLine(String.format("RMI Server @ %d > ", LocalPort));
    while (!input.equals("quit") && !input.equals("exit")) {
      String[] command = input.split(" ");
      int tokenLen = command.length;
      /* User feeds empty command. */
      if (tokenLen == 0) {
        continue;
      }
      /* Process `list` command. */
      if (command[0].equals("list")) {
        System.out
            .println("Following are the entries of Remote Object Reference table on local RMI Server:");
        RemoteObjectRef[] rorList = RoRTable.list();
        for (int i = 0; i < rorList.length; i++) {
          System.out.printf("Object Key: %d\n", rorList[i].getObjKey());
          System.out.printf("Remote Interface Name: %s\n", rorList[i].getRemoteInterfaceName());
          System.out.println("");
        }
        if (rorList.length == 0) {
          System.out.println("*** No objects registered on local RMI Server ***\n");
        }
      }
      /* Process `bind` command. */
      else if (command[0].equals("bind")) {
        if (tokenLen != 3) {
          System.out.println("Invalid arguments!");
          System.out.println("Usage: bind ServiceName RemoteObjectClass");
        } else {
          try {
            String srvName = command[1];
            String className = command[2];
            /* Obtains the Remote Implementation Class. */
            Class<?> remoteImpl;
            String remoteIntfClassName = null;
            remoteImpl = Class.forName(className);
            /* Populates RoR Table with the Remote Object. */
            long objKey = RoRTable.addObj(LocalHostName, LocalPort, remoteImpl.newInstance());
            /* Informs RMI Registry that the Service is ready. */
            if (remoteImpl.getInterfaces().length == 1) {
              remoteIntfClassName = remoteImpl.getInterfaces()[0].getName();
              String response =
                  RMIRegClient.bind(srvName, LocalHostName, LocalPort, objKey, remoteIntfClassName);
              if (response.equals("")) {
                System.out.printf("Successfully registered service %s!\n", srvName);
              }
              /* If anything fails, revert the change to local RoR Table. */
              else {
                RoRTable
                    .removeObj(new RemoteObjectRef(LocalHostName, LocalPort, objKey, className));
                System.out.printf(
                    "Error happened while registering service %s:%s.\nAll changes are reverted.\n",
                    srvName, response);
              }
            } else {
              System.out
                  .println("Error: Invalid Remote implementation class! Only one Servant interface is allowed.");
            }
          } catch (Exception e) {
            System.out.printf("Error occured during binding: %s\n", e.getMessage());
          }
        }
      }
      /* Process `rebind` command. */
      else if (command[0].equals("rebind")) {
        if (tokenLen != 3) {
          System.out.println("Invalid arguments!");
          System.out.println("Usage: bind ServiceName RemoteObjectClass");
        } else {
          try {
            String srvName = command[1];
            String className = command[2];
            /* Obtains the Remote Implementation Class. */
            Class<?> remoteImpl;
            String remoteIntfClassName = null;
            remoteImpl = Class.forName(className);
            System.err.printf("Error: Remote implementation class \"%s\" cannot be loaded!\n",
                className);
            /* Populates RoR Table with the Remote Object. */
            long objKey = RoRTable.addObj(LocalHostName, LocalPort, remoteImpl.newInstance());
            /* Informs RMI Registry that the Service is ready. */
            if (remoteImpl.getInterfaces().length == 1) {
              remoteIntfClassName = remoteImpl.getInterfaces()[0].getName();
              String response =
                  RMIRegClient.rebind(srvName, LocalHostName, LocalPort, objKey,
                      remoteIntfClassName);
              if (response.equals("")) {
                System.out.printf("Successfully registered service %s!\n", srvName);
              }
              /* If anything fails, revert the change to local RoR Table. */
              else {
                RoRTable
                    .removeObj(new RemoteObjectRef(LocalHostName, LocalPort, objKey, className));
                System.out.printf(
                    "Error happened while registering service %s:%s.\nAll changes are reverted.\n",
                    srvName, response);
              }
            } else {
              System.out
                  .println("Error: Invalid Remote implementation class! Only one Servant interface is allowed.");
            }
          } catch (Exception e) {
            System.out.printf("Error occured during rebinding: \n", e.getMessage());
          }
        }
      }
      /* Process `unbind` command. */
      else if (command[0].equals("unbind")) {
        if (tokenLen != 2 || !NumberUtils.isNumber(command[1])) {
          System.out.println("Invalid arguments!");
          System.out.println("Usage: unbind ObjectKey");
        } else {
          boolean error;
          /* First look up and remove the local RoR entry. */
          RemoteObjectRef ror = RoRTable.findRoRByObjKey(Long.parseLong(command[1]));
          error = (ror == null) || (!RoRTable.removeObj(ror));
          /* Then remove the reference in RMI Registry. */
          if (error) {
            System.out.printf("Failed to unbind object with Object Key %s in local RoR Table!\n",
                command[1]);
          } else {
            error = true;
            for (String srvName : RMIRegClient.list()) {
              if (ror.equals(RMIRegClient.lookup(srvName))) {
                RMIRegClient.unbind(srvName);
                error = false;
                break;
              }
            }
            if (error) {
              System.out.printf("Failed to unbind object with Object Key %s in RMI Registry!\n",
                  command[1]);
            } else {
              System.out.printf("Successfully unbound object with Object Key %s.\n", command[1]);
            }
          }
        }
      }
      /* Invalid command. */
      else {
        System.out
            .println("Invalid command! Enter an available command from below for its help message:");
        System.out.println("exit\tquit\tlist\tbind\trebind\tunbind");
      }
      input = console.readLine(String.format("RMI Server @ %d > ", LocalPort));
    }
    System.out.println("Cleaning up RMI Registry...");
    try {
      /* Cleans up RMI Registry */
    } catch (Exception e) {

    } finally {
      System.out.println("Done! Goodbye.");
      System.exit(0);
    }
  }
}

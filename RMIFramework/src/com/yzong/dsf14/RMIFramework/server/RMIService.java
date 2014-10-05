package com.yzong.dsf14.RMIFramework.server;

import java.net.ServerSocket;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.math.NumberUtils;

public class RMIService {

  private static String ClassName;
  private static String RegHostName;
  private static int RegPort;
  private static String ServiceName;
  private static int LocalPort;

  public static void main(String args[]) {
    if (args.length != 5 || !NumberUtils.isNumber(args[2]) || !NumberUtils.isNumber(args[4])) {
      System.err
          .println("Usage:\tjava -jar RMIService.jar ClassName RegistryHostname RegistryPort ServiceName LocalPort");
      System.exit(1);
    }
    ClassName = args[0];
    RegHostName = args[1];
    RegPort = Integer.parseInt(args[2]);
    ServiceName = args[3];
    LocalPort = Integer.parseInt(args[4]);
    Class mainClass;
    Class mainSkeleton;
    try {
      mainClass = Class.forName(ClassName);
      mainSkeleton = Class.forName(ClassName + "_skel");
    } catch (ClassNotFoundException e) {
      System.err.printf("ERROR -- Main class \"%s\" cannot be loaded!\n", ClassName);
      return;
    }
    RoREntryTable tbl = new RoREntryTable();
    try {
      ServerSocket srvSocket = new ServerSocket(LocalPort);
      while (true) {
        
      }
    } catch (Exception e) {
      System.err.println("ERROR -- Network I/O error occured!\n");
      return;
    }
    return;
  }

}

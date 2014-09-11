package com.yzong.dsf14;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.math.NumberUtils;

public class ProcManagerShell implements Runnable {

  private String localHost;
  private int localPort;
  private ConcurrentHashMap<String, String> clientIdToHostname =
      new ConcurrentHashMap<String, String>();
  private ConcurrentHashMap<String, Integer> clientIdToPort =
      new ConcurrentHashMap<String, Integer>();

  public ProcManagerShell(String lHost, int lPort) {
    localHost = lHost;
    localPort = lPort;
  }

  public synchronized ChildToMasterPackage SendPackageToChild(String child_hostname,
      int child_port, MasterToChildPackage pkg) {
    try {
      Socket childSocket = new Socket(child_hostname, child_port);
      ObjectOutputStream out = new ObjectOutputStream(childSocket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(childSocket.getInputStream());
      out.writeObject(pkg);
      ChildToMasterPackage output = (ChildToMasterPackage) in.readObject();
      childSocket.close();
      return output;
    } catch (IOException | ClassNotFoundException e) {
      return null;
    }
  }

  public void run() {
    // Before all, load job list.
    System.out.printf("Process Manager started at %s:%d.\n", localHost, localPort);
    // Process Manager Shell main loop.
    Console console = System.console();
    String input = console.readLine(String.format("Process Manager @ %d > ", localPort));
    while (!input.equals("quit") && !input.equals("exit")) {
      String[] command = input.split(" ");
      int tokenLen = command.length;
      if (tokenLen == 0) {
        continue;
      }
      // Handle child-registration command, in the format of:
      // register <hostname> <port> <child_id>
      if (command[0].equals("register")) {
        if (tokenLen != 4) {
          System.out.println("Incorrect number of arguments!");
          System.out.println("Usage: \t register <child_hostname> <child_port> <child_id>");
        } else if (!NumberUtils.isNumber(command[2])) {
          System.out.println("Argument child_port must be a number!");
        } else if (clientIdToHostname.containsKey(command[3])) {
          System.out.println("Child ID already used!");
        } else {
          String child_hostname = command[1];
          int child_port = Integer.parseInt(command[2]);
          String child_id = command[3];
          // This function checks the health of child node and informs it about parent node.
          boolean success =
              registerChildNode(child_hostname, child_port, localHost, localPort, child_id);
          if (success) {
            clientIdToHostname.put(child_id, child_hostname);
            clientIdToPort.put(child_id, child_port);
            System.out.println("Node registered.");
          } else {
            System.out.printf("Operation failed -- Node %s:%d unreachable or occupied!\n",
                child_hostname, child_port);
          }
        }

      }
      // Handle child-deregistration command, in the format of:
      // register <child_id>
      else if (command[0].equals("dereg")) {
        if (tokenLen != 2) {
          System.out.println("Incorrect number of arguments!");
          System.out.println("Usage: \t dereg <child_id>");
        } else if (!clientIdToHostname.containsKey(command[1])) {
          System.out.println("Child ID is not registered!");
        } else {
          String child_hostname = clientIdToHostname.get(command[1]);
          int child_port = clientIdToPort.get(command[1]);
          // This function attempts to kill the current job and dissociates it from parent node.
          boolean success = killAndDeregChildNode(child_hostname, child_port);
          if (success) {
            clientIdToHostname.remove(command[1]);
            clientIdToPort.remove(command[1]);
            System.out
                .printf("Child Node %s successfully disengaged and terminated.\n", command[1]);
          } else {
            System.out.printf("Operation failed -- Node %s:%d unreachable!\n", child_hostname,
                child_port);
          }
        }
      }
      // Handle child-listing command, in the format of:
      // list
      else if (command[0].equals("list")) {
        System.out.println("List of children and their jobs:");
        String[] childrenIds =
            clientIdToHostname.keySet().toArray(new String[clientIdToHostname.size()]);
        for (String id : childrenIds) {
          System.out.println(String.format("\nChild Hostname/Port: %s:%d", clientIdToHostname.get(id), clientIdToPort.get(id)));
          reportJobStatus(clientIdToHostname.get(id), clientIdToPort.get(id));
        }
      }
      // Handle job-creation command, in the format of:
      // run <child_id>
      // --> ClassName [args]
      else if (command[0].equals("run")) {
        if (tokenLen != 2) {
          System.out.println("Incorrect number of arguments!");
          System.out.println("Usage: \t run <child_id>");
        } else if (!clientIdToHostname.containsKey(command[1])) {
          System.out.println("Bad child node name!");
        } else {
          String jobDescription = console.readLine("Run Job > ");
          String[] tokens = jobDescription.split(" ");
          if (tokens.length == 0) {
            System.out.println("Please enter job name.");
          } else {
            String child_hostname = clientIdToHostname.get(command[1]);
            int child_port = clientIdToPort.get(command[1]);
            String response =
                createJob(child_hostname, child_port, tokens[0],
                    Arrays.copyOfRange(tokens, 1, tokens.length));
            if (response.equals("OK")) {
              System.out.printf("Job %s successfully created on Node %s.\n", tokens[0], command[1]);
            } else {
              System.out.printf("Job creation failed! Reason: %s\n", response);
            }
          }
        }
      }
      // Handle job-transfer command, in the format of:
      // transfer <task_id> <src_child_id> <dest_child_id>
      else if (command[0].equals("transfer")) {
        if (tokenLen != 4) {
          System.out.println("Incorrect number of arguments!");
          System.out.println("Usage: \t transfer <task_id> <src_child_id> <dest_child_id>");
        } else if (!clientIdToHostname.containsKey(command[2])
            || !clientIdToHostname.containsKey(command[3])) {
          System.out.println("Bad source/destination child node name!");
        } else {
          String task_id = command[1];
          String srcchild_hostname = clientIdToHostname.get(command[2]);
          int srcchild_port = clientIdToPort.get(command[2]);
          String destchild_hostname = clientIdToHostname.get(command[3]);
          int destchild_port = clientIdToPort.get(command[3]);
          System.out.println("Suspending source job and migrating job...");
          Object initReply = initiateMigration(task_id, srcchild_hostname, srcchild_port);
          if (initReply == null) {
            System.out.printf("Task migration failed: Network error with source node %s.\n",
                command[2]);
          } else if (!((ChildToMasterPackage) initReply).status.equals("OK")) {
            System.out.printf("Task migration failed: Error with source node %s -- %s\n",
                command[2], ((ChildToMasterPackage) initReply).message);
          } else {
            System.out.printf("Task info (%s) obtained from Node %s.\n", command[1], command[2]);
            boolean success =
                finalizeMigration(task_id,
                    (String) ((ChildToMasterPackage) initReply).argv[0],
                    (MigratableProcess) ((ChildToMasterPackage) initReply).argv[1],
                    destchild_hostname, destchild_port);
            if (success) {
              System.out.printf("Job migration of %s successful from Node %s to Node %s.\n", command[1], command[2],
                  command[3]);
            } else {
              System.out.println("Job migration failed: Error with destination node!");
            }
          }
        }
      }
      // Handle job-termination command, in the format of:
      // kill <child_id> <task_id>
      else if (command[0].equals("kill")) {
        if (tokenLen != 3) {
          System.out.println("Incorrect number of arguments!");
          System.out.println("Usage: \t kill <child_id> <task_id>");
        } else if (!clientIdToHostname.containsKey(command[1])) {
          System.out.println("Child ID does not exist!");
        } else {
          String child_hostname = clientIdToHostname.get(command[1]);
          int child_port = clientIdToPort.get(command[1]);
          String feedback = removeTaskFromHost(child_hostname, child_port, command[2]);
          if (feedback.equals("OK")) {
            System.out.printf("Killed task %s from child %s.\n", command[2], command[1]);
          } else {
            System.out.printf("Failed deleting task %s: %s\n", command[2], feedback);
          }
        }
      } else {
        System.out
            .println("Invalid command! Enter an available command from below for its help message:");
        System.out.println("exit\tquit\tregister\tdereg\tlist\trun\ttransfer\tkill");
      }
      // Grab new input.
      System.out.println("");
      input = console.readLine(String.format("Process Manager @ %d > ", localPort));
    }
    System.out.println("Informing childs to exit...");
    for (String childId : clientIdToPort.keySet()) {
      String child_hostname = clientIdToHostname.get(childId);
      int child_port = clientIdToPort.get(childId);
      boolean success = killAndDeregChildNode(child_hostname, child_port);
      if (success) {
        System.out.printf("Child Node %s successfully disengaged and terminated.\n", childId);
      } else {
        System.out.printf("Error while terminating child %s:%d. Please terminate it manually.\n",
            child_hostname, child_port);
      }
    }
    System.out.println("Done! Goodbye.");
    System.exit(0);
  }

  private String createJob(String child_hostname, int child_port, String class_name, String[] argv) {
    Object[] args = {class_name, argv};
    MasterToChildPackage pkg = new MasterToChildPackage("RUN", args);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    return reply.message;
  }

  private boolean finalizeMigration(String task_id, String task_jobname,
      MigratableProcess task_runnable, String child_hostname, int child_port) {
    Object[] args = {task_id, task_jobname, task_runnable};
    MasterToChildPackage pkg = new MasterToChildPackage("PUSH", args);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    return reply.status.equals("OK");
  }

  private Object initiateMigration(String task_id, String srcchild_hostname, int srcchild_port) {
    Object[] args = {task_id};
    MasterToChildPackage pkg = new MasterToChildPackage("PULL", args);
    ChildToMasterPackage reply = SendPackageToChild(srcchild_hostname, srcchild_port, pkg);
    return reply;
  }

  private void reportJobStatus(String child_hostname, int child_port) {
    MasterToChildPackage pkg = new MasterToChildPackage("REPORT", null);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    if (reply != null) {
      System.out.println(reply.message);
    }
  }

  private String removeTaskFromHost(String child_hostname, int child_port, String task_id) {
    Object[] arg = {task_id};
    MasterToChildPackage pkg = new MasterToChildPackage("KILL", arg);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    if (reply == null) {
      return "Network I/O error.";
    }
    return reply.message;
  }

  private boolean killAndDeregChildNode(String child_hostname, int child_port) {
    MasterToChildPackage pkg = new MasterToChildPackage("KILLALL", null);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    if (reply == null) {
      return false;
    }
    return reply.status.equals("OK");
  }

  private boolean registerChildNode(String child_hostname, int child_port, String localHost,
      int localPort, String child_id) {
    Object[] args = {localHost, localPort, child_id};
    MasterToChildPackage pkg = new MasterToChildPackage("REG", args);
    ChildToMasterPackage reply = SendPackageToChild(child_hostname, child_port, pkg);
    if (reply == null) {
      return false;
    }
    return reply.status.equals("OK");
  }
}

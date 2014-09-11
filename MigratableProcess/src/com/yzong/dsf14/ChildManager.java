package com.yzong.dsf14;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;

import com.yzong.dsf14.MigratableProcesses.CopyProcess;
import com.yzong.dsf14.MigratableProcesses.GrepProcess;
import com.yzong.dsf14.MigratableProcesses.StatProcess;

public class ChildManager implements Runnable {

  private boolean isAlive = true;
  private String remoteHost = null;
  private int remotePort = -1;
  private int localPort;
  private String childName = null;
  private ConcurrentHashMap<String, String> taskIdToName = new ConcurrentHashMap<String, String>();
  private ConcurrentHashMap<String, Thread> taskIdToThread =
      new ConcurrentHashMap<String, Thread>();
  private ConcurrentHashMap<String, MigratableProcess> taskIdToRunnable =
      new ConcurrentHashMap<String, MigratableProcess>();
  private ConcurrentHashMap<String, Class<? extends MigratableProcess>> jobNameToJob =
      new ConcurrentHashMap<String, Class<? extends MigratableProcess>>();

  public ChildManager(int lPort) {
    this.localPort = lPort;
    this.isAlive = true;
  }

  @SuppressWarnings("deprecation")
  public synchronized ChildToMasterPackage processMasterPkg(MasterToChildPackage pkg) {
    ChildToMasterPackage response = new ChildToMasterPackage("XXX", "XXX", "XXX");
    // First of all, refresh Alive Task List.
    for (String taskId : taskIdToName.keySet().toArray(new String[taskIdToName.size()])) {
      if (!taskIdToThread.get(taskId).isAlive()) {
        taskIdToName.remove(taskId);
        taskIdToThread.remove(taskId);
        taskIdToRunnable.remove(taskId);
        System.out.printf("Info: Automatic check detected task ID %s has finished.\n", taskId);
      }
    }
    // Handle Child Registration.
    if (pkg.command.equals("REG")) {
      String masterHost = (String) pkg.argv[0];
      int masterPort = (int) pkg.argv[1];
      String childId = (String) pkg.argv[2];
      if (this.remoteHost == null || this.remotePort == -1) {
        this.childName = childId;
        this.remoteHost = masterHost;
        this.remotePort = masterPort;
        response = new ChildToMasterPackage("REG", "OK", "OK");
        System.out.printf("Info: Connected with Master Node %s:%d as ID=%s.\n", masterHost,
            masterPort, childId);
      } else {
        response = new ChildToMasterPackage("REG", "ERR", "Node already occupied.");
        System.out.printf("Warning: Master %s:%d attempted to bind an occupied child node!\n",
            masterHost, masterPort);
      }
    }
    // If child is unregistered, deny all requests.
    else if (this.remoteHost == null || this.remotePort == -1) {
      response = new ChildToMasterPackage(pkg.command, "ERR", "Child not registered.");
      System.out.println("Error: Master attempted to communicate with orphan child node!");
    }
    // Handle List Task Request
    else if (pkg.command.equals("REPORT")) {
      String statusReport = "";
      statusReport += String.format("Child Name: %s\n", this.childName);
      statusReport += String.format("Registered Master Hostname: %s\n", this.remoteHost);
      statusReport += String.format("Registered Master Port: %d\n", this.remotePort);
      statusReport += String.format("Active Job List: (Number of Jobs: %d)\n", taskIdToName.size());
      for (String taskId : taskIdToName.keySet()) {
        statusReport +=
            String.format("\t Task Id: %s\t Task Name: %s\n", taskId, taskIdToName.get(taskId));
      }
      response = new ChildToMasterPackage("REPORT", "OK", statusReport);
      System.out.println("Info: Handled REPORT request from Master.");
    // Handle task running request.
    } else if (pkg.command.equals("RUN")) {
      String className = (String) pkg.argv[0];
      String[] argv = (String[]) pkg.argv[1];
      if (!jobNameToJob.containsKey(className)) {
        response = new ChildToMasterPackage("RUN", "ERR", "Job not found.");
        System.out
            .printf("Error: Master Node requested RUN with unknown job name %s!\n", className);
      } else {
        String taskId = RandomStringUtils.randomAlphanumeric(8);
        Class<? extends MigratableProcess> taskClass = jobNameToJob.get(className);
        try {
          Constructor<? extends MigratableProcess> taskClassConstructor =
              taskClass.getConstructor(String[].class);
          MigratableProcess taskProcess = taskClassConstructor.newInstance(new Object[] {argv});
          Thread taskThread = new Thread(taskProcess);
          taskThread.start();
          taskIdToName.put(taskId, className);
          taskIdToRunnable.put(taskId, taskProcess);
          taskIdToThread.put(taskId, taskThread);
          response = new ChildToMasterPackage("RUN", "OK", "OK");
          System.out.printf("Info: Created task %s with ID %s.\n", className, taskId);
        } catch (NoSuchMethodException | SecurityException | InstantiationException
            | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
          response =
              new ChildToMasterPackage("RUN", "ERR", String.format("Job failed to initialize: %s",
                  e.getMessage()));
          System.out.printf(
              "Error: Job %s (ID: %s) failed to initialize! Error: %s. Stack Trace:\n", className,
              taskId, e.getMessage());
          e.printStackTrace();
        }
      }
    // Handle job pulling request (first half of job migration).
    } else if (pkg.command.equals("PULL")) {
      String taskId = (String) pkg.argv[0];
      if (!taskIdToThread.containsKey(taskId)) {
        response = new ChildToMasterPackage("PULL", "ERR", "Task ID not found.");
        System.out.printf("Error: Master attempted to migrate nonexistent Task with ID %s!\n",
            taskId);
      } else {
        // Obtain the task thread and runnable. Suspend runnable and terminate thread.
        MigratableProcess taskRunnable = taskIdToRunnable.get(taskId);
        Thread taskThread = taskIdToThread.get(taskId);
        String taskJobName = taskIdToName.get(taskId);
        taskRunnable.suspend();
        try {
          taskThread.join(3000);
          taskThread.stop();
        } catch (InterruptedException e) {
        }
        // Remove the task record on child node.
        taskIdToName.remove(taskId);
        taskIdToThread.remove(taskId);
        taskIdToRunnable.remove(taskId);
        // Send out MigratableProcess as object.
        response = new ChildToMasterPackage("PULL", "OK", "OK");
        Object[] argv = {taskJobName, taskRunnable};
        response.setArgv(argv);
        System.out.printf("Info: Task with ID %s was PULLed by master node.\n", taskId);
      }
    // Handle job pushing request (second half of job migration).
    } else if (pkg.command.equals("PUSH")) {
      String taskId = (String) pkg.argv[0];
      String taskJobName = (String) pkg.argv[1];
      MigratableProcess taskRunnable = (MigratableProcess) pkg.argv[2];
      // Create task record on the child node.
      taskIdToName.put(taskId, taskJobName);
      taskIdToRunnable.put(taskId, taskRunnable);
      Thread taskThread = new Thread(taskRunnable);
      taskIdToThread.put(taskId, taskThread);
      taskThread.start();
      response = new ChildToMasterPackage("PUSH", "OK", "OK");
      System.out.printf("Info: Resumed task %s with ID %s.\n", taskJobName, taskId);
    // Handle job kill request.
    } else if (pkg.command.equals("KILL")) {
      String taskId = (String) pkg.argv[0];
      if (!taskIdToThread.containsKey(taskId)) {
        response =
            new ChildToMasterPackage("KILL", "ERR", String.format("Failed to locate job %s.",
                taskId));
        System.out.printf("Error: Attempted to KILL nonexistent Task with ID %s.\n", taskId);
      } else {
        Thread taskThread = taskIdToThread.get(taskId);
        MigratableProcess taskClass = taskIdToRunnable.get(taskId);
        taskClass.suspend();
        try {
          taskThread.join(3000);
          taskThread.stop();
        } catch (InterruptedException e) {
        }
        taskIdToName.remove(taskId);
        taskIdToThread.remove(taskId);
        taskIdToRunnable.remove(taskId);
        response = new ChildToMasterPackage("KILL", "OK", "OK");
        System.out.printf("Info: Successfully KILLed task with ID %s.\n", taskId);
      }
    } else if (pkg.command.equals("KILLALL")) {
      // Terminate all running tasks.
      for (String taskId : taskIdToName.keySet()) {
        Thread taskThread = taskIdToThread.get(taskId);
        MigratableProcess taskClass = taskIdToRunnable.get(taskId);
        taskClass.suspend();
        try {
          taskThread.join(3000);
          taskThread.stop();
        } catch (InterruptedException e) {
        }
      }
      // Terminate Child Node itself.
      this.isAlive = false;
      response = new ChildToMasterPackage("KILLALL", "OK", "OK");
      System.out.println("Info: Successfully KILLALL and terminated Child Node.");
    } else {
      System.out.printf("Error: Master Node sent an unknown command %s!\n", pkg.command);
    }
    return response;
  }

  public void run() {
    // Before all, load job list.
    jobNameToJob.put("GrepProcess", GrepProcess.class);
    jobNameToJob.put("CopyProcess", CopyProcess.class);
    jobNameToJob.put("StatProcess", StatProcess.class);
    // Starting up child server.
    System.out.printf("Starting up Child Manager at port %d...\n", localPort);
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(localPort);
      while (this.isAlive) {
        try {
          Socket clientSocket = serverSocket.accept();
          ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
          ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
          ChildToMasterPackage output = processMasterPkg((MasterToChildPackage) (in.readObject()));
          out.writeObject(output);
          out.flush();
        } catch (ClassNotFoundException e) {
          System.out.println("Error while processing client request.");
        }
      }
    } catch (IOException e) {
      System.err.printf("Exception caught when binding port %d.\n", localPort);
      e.printStackTrace();
      System.exit(1);
    } finally {
      try {
        serverSocket.close();
      } catch (IOException e) {
      }
      System.out.println("Goodbye!");
    }
  }

}

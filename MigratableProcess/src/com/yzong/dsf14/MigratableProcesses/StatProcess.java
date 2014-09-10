package com.yzong.dsf14.MigratableProcesses;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.Date;

import com.yzong.dsf14.MigratableProcess;
import com.yzong.dsf14.TransactionalFileOutputStream;

public class StatProcess implements MigratableProcess {

  private static final long serialVersionUID = 7199546965587806734L;
  private TransactionalFileOutputStream outFile;
  private int lineNum = 0;
  private volatile boolean suspending = false;

  public StatProcess(String args[]) throws Exception {
    if (args.length != 2) {
      System.out.println("usage: StatProcess <lineNumber> <outputFile>");
      throw new Exception("Invalid Arguments");
    }
    this.outFile = new TransactionalFileOutputStream(args[1], false);
    this.lineNum = Integer.parseInt(args[0]);
  }

  @Override
  public void run() {
    PrintStream out = new PrintStream(outFile);
    try {
      while (!suspending) {

        if (lineNum <= 0)
          break;

        out.println(String.format("Hello from Host %s at UTC %s", InetAddress.getLocalHost()
            .getHostName(), new Date().toString()));
        lineNum--;

        // Make the process longer
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
        }
      }
    } catch (IOException e) {
      System.out.println("GrepProcess: Error: " + e);
    }

    suspending = false;
  }

  @Override
  public void suspend() {
    suspending = true;
    while (suspending);
  }

}

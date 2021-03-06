package com.yzong.dsf14.MigratableProcesses;

import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

import com.yzong.dsf14.MigratableProcess;
import com.yzong.dsf14.TransactionalFileInputStream;
import com.yzong.dsf14.TransactionalFileOutputStream;

public class GrepProcess implements MigratableProcess {

  private static final long serialVersionUID = 7199546965587806734L;
  private TransactionalFileInputStream inFile;
  private TransactionalFileOutputStream outFile;
  private String query;

  private volatile boolean suspending;

  public GrepProcess(String args[]) throws Exception {
    if (args.length != 3) {
      System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
      throw new Exception("Invalid Arguments");
    }
    query = args[0];
    inFile = new TransactionalFileInputStream(args[1]);
    outFile = new TransactionalFileOutputStream(args[2], false);
  }

  public void run() {
    PrintStream out = new PrintStream(outFile);
    DataInputStream in = new DataInputStream(inFile);
    try {
      while (!suspending) {
        @SuppressWarnings("deprecation")
        String line = in.readLine();

        if (line == null)
          break;

        if (line.contains(query)) {
          out.println(line);
        }
        // Make the process longer
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
      }
    } catch (EOFException e) {
    } catch (IOException e) {
      System.out.println("GrepProcess: Error: " + e);
    }

    suspending = false;
  }

  public void suspend() {
    suspending = true;
    while (suspending);
  }

}

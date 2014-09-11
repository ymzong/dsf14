package com.yzong.dsf14.MigratableProcesses;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

import com.yzong.dsf14.MigratableProcess;
import com.yzong.dsf14.TransactionalFileInputStream;
import com.yzong.dsf14.TransactionalFileOutputStream;

public class CopyProcess implements MigratableProcess {

  private static final long serialVersionUID = 7199546965587806734L;
  private TransactionalFileInputStream inFile;
  private TransactionalFileOutputStream outFile;
  private String signature = "";
  private int lineNum = 0;
  private volatile boolean suspending = false;

  public CopyProcess(String args[]) throws Exception {
    if (args.length != 3) {
      System.out.println("usage: CopyProcess <signature> <inputFile> <outputFile>");
      throw new Exception("Invalid Arguments");
    }
    this.signature = args[0];
    this.inFile = new TransactionalFileInputStream(args[1]);
    this.outFile = new TransactionalFileOutputStream(args[2], false);
    this.lineNum = 0;
  }

  @Override
  public void run() {
    PrintStream out = new PrintStream(outFile);
    DataInputStream in = new DataInputStream(inFile);
    try {
      while (!suspending) {
        @SuppressWarnings("deprecation")
        String line = in.readLine();

        if (line == null)
          break;

        out.println(String.format("%d %s %s", lineNum, line, signature));
        lineNum++;

        // Make the process longer
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
        }
      }
    } catch (EOFException e) {
    } catch (IOException e) {
      System.out.println("CopyProcess: Error: " + e);
    }

    suspending = false;
  }

  @Override
  public void suspend() {
    suspending = true;
    while (suspending);
  }

}

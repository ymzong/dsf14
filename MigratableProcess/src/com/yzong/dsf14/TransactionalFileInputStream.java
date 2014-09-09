package com.yzong.dsf14;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable {

  private static final long serialVersionUID = 7225615748284297386L;
  private String fileName;
  private long fileOffset;
  
  public TransactionalFileInputStream(String inFileName) {
    this.fileName = inFileName;
    this.fileOffset = 0L;
  }

  @Override
  public synchronized int read() throws IOException {
    File inFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(inFile, "r");
    Raf.seek(fileOffset);
    int b = Raf.read();
    this.fileOffset++;
    Raf.close();
    return b;
  }

  @Override
  public synchronized int read(byte[] b) throws IOException {
    File inFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(inFile, "r");
    Raf.seek(fileOffset);
    int l = Raf.read(b);
    this.fileOffset += l;
    Raf.close();
    return l;
  }
  
  @Override
  public synchronized int read(byte[] b, int off, int len) throws IOException {
    File inFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(inFile, "r");
    Raf.seek(fileOffset);
    int l = Raf.read(b, off, len);
    this.fileOffset += l;
    Raf.close();
    return l;
  }
  
}

package com.yzong.dsf14;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

  private static final long serialVersionUID = -6715459623958812378L;
  private String fileName;
  private long fileOffset;

  public TransactionalFileOutputStream(String outFileName) {
    this.fileName = outFileName;
    this.fileOffset = 0L;
  }

  public TransactionalFileOutputStream(String outFileName, boolean append) {
    this.fileName = outFileName;
    if (append) {
      File outFile = new File(fileName);
      long fileLen = 0;
      try {
        RandomAccessFile Raf = new RandomAccessFile(outFile, "r");
        fileLen = Raf.length();
        Raf.close();
      } catch (IOException e) {
      }
      this.fileOffset = fileLen;
    } else {
      this.fileOffset = 0;
    }
  }

  @Override
  public synchronized void write(int b) throws IOException {
    File outFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(outFile, "rw");
    Raf.seek(fileOffset);
    Raf.write(b);
    this.fileOffset++;
    Raf.close();
  }

  @Override
  public synchronized void write(byte[] b) throws IOException {
    File outFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(outFile, "rw");
    Raf.seek(fileOffset);
    Raf.write(b);
    this.fileOffset += b.length;
    Raf.close();
  }

  @Override
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    File outFile = new File(fileName);
    RandomAccessFile Raf = new RandomAccessFile(outFile, "rw");
    Raf.seek(fileOffset);
    Raf.write(b, off, len);
    this.fileOffset += len;
    Raf.close();
  }

}

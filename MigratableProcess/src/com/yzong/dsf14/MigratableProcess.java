package com.yzong.dsf14;

import java.io.Serializable;

public interface MigratableProcess extends Runnable, Serializable {

  public TransactionalFileInputStream inFile = null;
  public TransactionalFileOutputStream outFile = null;

  public boolean suspending = false;

  public void run();

  public void suspend();

}

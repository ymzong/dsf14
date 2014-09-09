package com.yzong.dsf14;

import java.io.Serializable;

public class MasterToChildPackage implements Serializable {

  private static final long serialVersionUID = -3843722160285630232L;
  public String command = "";
  public Object[] argv = {};

  public MasterToChildPackage(String command, Object[] argv) {
    this.command = command;
    this.argv = argv;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Object[] getArgv() {
    return argv;
  }

  public void setArgv(Object[] argv) {
    this.argv = argv;
  }

}

package com.yzong.dsf14;

import java.io.Serializable;

public class ChildToMasterPackage implements Serializable {

  private static final long serialVersionUID = -1736414151185372441L;
  public String command = "";
  public String status = "";
  public String message = "";
  public Object[] argv = {};
  
  public ChildToMasterPackage(String cmd, String stat, String msg) {
    this.command = cmd;
    this.status = stat;
    this.message = msg;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object[] getArgv() {
    return argv;
  }

  public void setArgv(Object[] argv) {
    this.argv = argv;
  }
  
}

package com.yzong.dsf14;

public class ChildSideProtocol {

  public Object process(Object obj) {
    try {
      MasterToChildPackage pkg = (MasterToChildPackage) obj;
      if (pkg.command == "") {
        
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

}

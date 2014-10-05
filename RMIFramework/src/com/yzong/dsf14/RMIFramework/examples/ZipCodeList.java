package com.yzong.dsf14.RMIFramework.examples;

public class ZipCodeList {
  String city;
  String ZipCode;
  ZipCodeList next;

  public ZipCodeList(String c, String z, ZipCodeList n) {
    city = c;
    ZipCode = z;
    next = n;
  }
}

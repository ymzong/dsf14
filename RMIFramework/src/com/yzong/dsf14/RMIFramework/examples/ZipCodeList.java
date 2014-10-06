package com.yzong.dsf14.RMIFramework.examples;

/**
 * ZipCodeList is a linked list with two data fields: <tt>City</tt> and <tt>ZipCode</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
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

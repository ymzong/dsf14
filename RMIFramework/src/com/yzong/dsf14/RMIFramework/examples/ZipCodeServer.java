package com.yzong.dsf14.RMIFramework.examples;

import com.yzong.dsf14.RMIFramework.server.RMIRemoteStub;

public interface ZipCodeServer extends RMIRemoteStub
{
    public void initialise(ZipCodeList newlist);
    public String find(String city);
    public ZipCodeList findAll();
    public void printAll();
}

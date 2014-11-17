package com.yzong.dsf14.mapred.runnable;

import java.io.Console;

import com.yzong.dsf14.mapred.dfs.DfsController;
import com.yzong.dsf14.mapred.framework.MapRedController;

public class MapRedMasterCLI implements Runnable {

  public DfsController DC;
  public MapRedController MC;
  
  public MapRedMasterCLI(DfsController dfsController, MapRedController mrController) {
    this.DC = dfsController;
    this.MC = mrController;
  }
  
  @Override
  public void run() {
    /* Start the DFS Master Shell. */
    Console console = System.console();
    String input = console.readLine("DFS Master > ");
    while (!input.equals("quit") && !input.equals("exit")) {
      // TODO: mainloop.
      DC.putFile("syslog", "coolstuff");
      input = console.readLine("\nDFS Master > ");
      DC.getFile("coolstuff", "resultingfile");
      input = console.readLine("\nBlah");
    }
  }

}

package com.yzong.dsf14.mapred.dfs;

/**
 * Contain the metadata for a file on the distributed file system, i.e. <tt>FileName</tt>,
 * <tt>RecordSize</tt>, and <tt>NumShards</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class FileProp {
  public String FileName;
  public int RecordSize;
  public int NumShards;

}

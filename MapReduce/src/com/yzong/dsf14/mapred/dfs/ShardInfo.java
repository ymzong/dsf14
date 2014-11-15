package com.yzong.dsf14.mapred.dfs;

/**
 * Represents a segment of file on a Worker node. Contains <tt>FileName</tt>, <tt>ShardIndex</tt>,
 * and <tt>RemotePath</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class ShardInfo {
  public String FileName;
  public int ShardIndex;
  public String RemotePath;

  public ShardInfo(String fileName, int shardIndex, String remotePath) {
    FileName = fileName;
    ShardIndex = shardIndex;
    RemotePath = remotePath;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((FileName == null) ? 0 : FileName.hashCode());
    result = prime * result + ShardIndex;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ShardInfo other = (ShardInfo) obj;
    if (FileName == null) {
      if (other.FileName != null)
        return false;
    } else if (!FileName.equals(other.FileName))
      return false;
    if (ShardIndex != other.ShardIndex)
      return false;
    return true;
  }

}

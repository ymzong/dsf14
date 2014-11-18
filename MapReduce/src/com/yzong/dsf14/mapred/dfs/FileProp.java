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

  public FileProp(String fileName, int recordSize, int numShards) {
    FileName = fileName;
    RecordSize = recordSize;
    NumShards = numShards;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((FileName == null) ? 0 : FileName.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FileProp other = (FileProp) obj;
    if (FileName == null) {
      if (other.FileName != null)
        return false;
    } else if (!FileName.equals(other.FileName))
      return false;
    return true;
  }

}

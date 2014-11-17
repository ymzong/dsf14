package com.yzong.dsf14.mapred.framework;

/**
 * Wrapper interface that collects data output by <tt>Mapper</tt> or <tt>Reducer</tt>.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 * @param <Key>
 * @param <Value>
 */
public interface OutputCollector<Key, Value> {

  /**
   * Adds a key-value pair to the output.
   * 
   * @param k Key to collect.
   * @param v Value to collect.
   */
  void collect(Key k, Value v);

}

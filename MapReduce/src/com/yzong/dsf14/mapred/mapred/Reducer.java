package com.yzong.dsf14.mapred.mapred;

import java.io.IOException;
import java.util.Iterator;

/**
 * Interface for a Reducer class with a <tt>reduce</tt> function, which a user will implement.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 * @param <InputKey> Type for intermediate key.
 * @param <InputVal> Type for intermediate value.
 * @param <OutputKey> Type for final output key.
 * @param <OutputVal> Type for final output value.
 */
public interface Reducer<InputKey, InputVal, OutputKey, OutputVal> {

  /**
   * Maps an intermediate key and its values to (potentially many) final Key''-Value'' pairs.
   * 
   * @param key Input key.
   * @param value Iterator of input values corresponding to <tt>key</tt>.
   * @param output Collector for output keys and values.
   * @param reporter Progress reporter.
   * @throws IOException
   */
  void reduce(InputKey key, Iterator<InputVal> values,
      OutputCollector<OutputKey, OutputVal> output, Reporter reporter) throws IOException;

}

package com.yzong.dsf14.mapred.mapred;

import java.io.IOException;

/**
 * Interface for a Mapper class that contains a <tt>map</tt> function, which a user will implement.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 * @param <InputKey> Type for input key.
 * @param <InputVal> Type for input value.
 * @param <OutputKey> Type for intermediate output key.
 * @param <OutputVal> Type for intermediate output value.
 */
public interface Mapper<InputKey, InputVal, OutputKey, OutputVal> {

  /**
   * Maps a Key-Value pair to (potentially many) intermediate Key'-Value' pairs.
   * 
   * @param key Input key.
   * @param value Input value.
   * @param output Collector for output keys and values.
   * @param reporter Progress reporter.
   * @throws IOException
   */
  public void map(InputKey key, InputVal value, OutputCollector<OutputKey, OutputVal> output,
      Reporter reporter) throws IOException;
}

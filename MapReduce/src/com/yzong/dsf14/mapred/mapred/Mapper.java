package com.yzong.dsf14.mapred.mapred;

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

  public void map(InputKey key, InputVal value, OutputCollector<OutputKey, OutputVal> output,
      Reporter reporter);
}

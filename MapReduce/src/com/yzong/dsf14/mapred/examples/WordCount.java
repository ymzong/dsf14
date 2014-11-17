/**
 * 
 */
package com.yzong.dsf14.mapred.examples;

import java.io.IOException;
import java.util.Iterator;

import com.yzong.dsf14.mapred.mapred.Mapper;
import com.yzong.dsf14.mapred.mapred.OutputCollector;
import com.yzong.dsf14.mapred.mapred.Reporter;
import com.yzong.dsf14.mapred.mapred.io.IntWritable;
import com.yzong.dsf14.mapred.mapred.io.LongWritable;
import com.yzong.dsf14.mapred.mapred.io.TextWritable;

/**
 * Classic Example: Word Count job that counts the total number of words in a huge document.
 * (Credit: Hadoop official example <tt>WordCount v1.0</tt>.)
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class WordCount {
  public static class Map implements Mapper<LongWritable, TextWritable, TextWritable, IntWritable> {

    @Override
    public void map(LongWritable key, TextWritable value,
        OutputCollector<TextWritable, IntWritable> output, Reporter reporter) throws IOException {
      // TODO Auto-generated method stub

    }
  }

  public static class Reduce implements
      Reducer<TextWritable, IntWritable, TextWritable, IntWritable> {
    @Override
    public void reduce(TextWritable key, Iterator<IntWritable> values,
        OutputCollector<TextWritable, IntWritable> output, Reporter reporter) throws IOException {
      // TODO Auto-generated method stub

    }
  }
}

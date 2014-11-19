/**
 * 
 */
package com.yzong.dsf14.mapred.examples;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.yzong.dsf14.mapred.framework.MapRedJobClient;
import com.yzong.dsf14.mapred.framework.MapRedJobConf;
import com.yzong.dsf14.mapred.framework.Mapper;
import com.yzong.dsf14.mapred.framework.OutputCollector;
import com.yzong.dsf14.mapred.framework.Reducer;
import com.yzong.dsf14.mapred.framework.Reporter;
import com.yzong.dsf14.mapred.io.IntWritable;
import com.yzong.dsf14.mapred.io.LongWritable;
import com.yzong.dsf14.mapred.io.SingularInputFormat;
import com.yzong.dsf14.mapred.io.TextWritable;

/**
 * Classic Example: Word Count job that counts the total number of words in a huge document.
 * (Credit: Hadoop official example <tt>WordCount v1.0</tt>.)
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class WordCount {

  /**
   * User implementation of WordCount Mapper class.
   * 
   * @author Jimmy Zong <yzong@cmu.edu>
   *
   */
  public static class Map implements Mapper<LongWritable, TextWritable, TextWritable, IntWritable> {

    private final static IntWritable one = new IntWritable(1);
    private TextWritable word = new TextWritable("");

    @Override
    public void map(LongWritable key, TextWritable value,
        OutputCollector<TextWritable, IntWritable> output, Reporter reporter) throws IOException {
      String line = value.toString();
      StringTokenizer tokenizer = new StringTokenizer(line);
      while (tokenizer.hasMoreTokens()) {
        word.setValue(tokenizer.nextToken());
        output.collect(word, one);
      }
      return;
    }
  }

  /**
   * User implementation of WordCount Reducer class.
   * 
   * @author Jimmy Zong <yzong@cmu.edu>
   *
   */
  public static class Reduce implements
      Reducer<TextWritable, IntWritable, TextWritable, IntWritable> {
    @Override
    public void reduce(TextWritable key, Iterator<IntWritable> values,
        OutputCollector<TextWritable, IntWritable> output, Reporter reporter) throws IOException {
      int sum = 0;
      while (values.hasNext()) {
        sum += values.next().getValue();
      }
      output.collect(key, new IntWritable(sum));
    }
  }

  /**
   * This is where user can specify the configuration of a MapReduce job.
   */
  public static MapRedJobConf getMapReduceConf() {
    /* Populate job configurations. */
    MapRedJobConf conf = new MapRedJobConf(WordCount.class); // New config from current class.
    conf.setMapRedJobName("jimmyswordcount"); // MapReduce job name.
    conf.setInputFormat(new SingularInputFormat(TextWritable.class)); // Input format.
    conf.setInputPath("wordcount.input"); // Input file path.
    conf.setOutputPath("wordcount.output"); // Output file path.
    conf.setMapperClass(Map.class); // Mapper Class.
    conf.setReducerClass(Reduce.class); // Reducer Class.
    return conf;
  }
}

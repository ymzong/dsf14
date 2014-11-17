/**
 * 
 */
package com.yzong.dsf14.mapred.examples;

import java.io.IOException;
import java.util.Iterator;

import com.yzong.dsf14.mapred.mapred.MapRedJobClient;
import com.yzong.dsf14.mapred.mapred.MapRedJobConf;
import com.yzong.dsf14.mapred.mapred.Mapper;
import com.yzong.dsf14.mapred.mapred.OutputCollector;
import com.yzong.dsf14.mapred.mapred.Reducer;
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

  /**
   * Entry point for the MapReduce job.
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    /* Sanity check for number of arguments. */
    if (args.length < 2) {
      System.err.println("Please put `inputPath` and `outputPath` as first two arguments.");
      System.exit(1);
    }
    /* Populate job configurations. */
    MapRedJobConf conf = new MapRedJobConf(WordCount.class);
    conf.setMapRedJobName("jimmyswordcount");
    conf.setInputPath(args[0]);
    conf.setOutputPath(args[1]);
    conf.setMapperClass(Map.class);
    conf.setReducerClass(Reduce.class);
    /* Spin up the job. */
    MapRedJobClient mrClient = new MapRedJobClient(conf);
    boolean success = mrClient.run(true); // Wait for job to complete
    if (success) {
      System.out.println("MapReduce job succeeded!");
      System.exit(0);
    }
    else {
      System.out.println("MapReduce job failed! See error message above for details.");
      System.exit(1);
    }
  }
}

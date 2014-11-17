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
   * Entry point for the MapReduce job.
   * 
   * @param args [<tt>inputPath</tt>, <tt>outputPath</tt>] for the MapReduce job.
   */
  public static void main(String[] args) {
    /* Sanity check for number of arguments. */
    if (args.length < 2) {
      System.err.println("Please put `inputPath` and `outputPath` as first two arguments.");
      System.exit(1);
    }
    /* Populate job configurations. */
    MapRedJobConf conf = new MapRedJobConf(WordCount.class); // New config from current class.
    conf.setMapRedJobName("jimmyswordcount"); // MapReduce job name.
    conf.setInputFormat(new SingularInputFormat(TextWritable.class)); // Input format.
    conf.setInputPath(args[0]); // Input file path.
    conf.setOutputPath(args[1]); // Output file path.
    conf.setMapperClass(Map.class); // Mapper Class.
    conf.setReducerClass(Reduce.class); // Reducer Class.
    /* Spin up the job. */
    MapRedJobClient mrClient = new MapRedJobClient(conf); // Initiate MR client from job Config.
    boolean success = mrClient.run(true); // Wait for job to complete
    if (success) {
      System.out.println("MapReduce job succeeded!");
      System.exit(0);
    } else {
      System.out.println("MapReduce job failed! See error message above for details.");
      System.exit(1);
    }
  }
}

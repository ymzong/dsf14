package com.yzong.dsf14.mapred.framework;

import org.apache.commons.lang.RandomStringUtils;

import com.yzong.dsf14.mapred.io.InputFormat;

/**
 * Contains all config parameters of a MapReduce job.
 * 
 * @author Jimmy Zong <yzong@cmu.edu>
 *
 */
public class MapRedJobConf {

  private Class<?> MapRedDefinition;
  private String MapRedJobName = RandomStringUtils.randomAlphanumeric(8);
  private InputFormat InputFormat;
  private String InputPath = null;
  private String OutputPath = null;
  private Class<?> MapperClass = null;
  private Class<?> ReducerClass = null;

  /**
   * Initializes a MapReduce job configuration with a definition class.
   * 
   * @param mapRedDefinition
   */
  public MapRedJobConf(Class<?> mapRedDefinition) {
    setMapRedDefinition(mapRedDefinition);
  }

  /**
   * @return the mapRedJobName
   */
  public String getMapRedJobName() {
    return MapRedJobName;
  }

  /**
   * @param mapRedJobName the mapRedJobName to set
   */
  public void setMapRedJobName(String mapRedJobName) {
    MapRedJobName = mapRedJobName;
  }

  /**
   * @return the inputPath
   */
  public String getInputPath() {
    return InputPath;
  }

  /**
   * @param inputPath the inputPath to set
   */
  public void setInputPath(String inputPath) {
    InputPath = inputPath;
  }

  /**
   * @return the outputPath
   */
  public String getOutputPath() {
    return OutputPath;
  }

  /**
   * @param outputPath the outputPath to set
   */
  public void setOutputPath(String outputPath) {
    OutputPath = outputPath;
  }

  /**
   * @return the mapperClass
   */
  public Class<?> getMapperClass() {
    return MapperClass;
  }

  /**
   * @param mapperClass the mapperClass to set
   */
  public void setMapperClass(Class<?> mapperClass) {
    MapperClass = mapperClass;
  }

  /**
   * @return the reducerClass
   */
  public Class<?> getReducerClass() {
    return ReducerClass;
  }

  /**
   * @param reducerClass the reducerClass to set
   */
  public void setReducerClass(Class<?> reducerClass) {
    ReducerClass = reducerClass;
  }

  /**
   * @return the mapRedDefinition
   */
  public Class<?> getMapRedDefinition() {
    return MapRedDefinition;
  }

  /**
   * @param mapRedDefinition the mapRedDefinition to set
   */
  public void setMapRedDefinition(Class<?> mapRedDefinition) {
    MapRedDefinition = mapRedDefinition;
  }

  /**
   * @return the inputFormat
   */
  public InputFormat getInputFormat() {
    return InputFormat;
  }

  /**
   * @param inputFormat the inputFormat to set
   */
  public void setInputFormat(InputFormat inputFormat) {
    InputFormat = inputFormat;
  }

}

package com.yzong.dsf14.openMPIKMeans.euclidean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entry point for sequential K-Means algorithm in 2D Euclidean space.
 * 
 * @author Yiming Zong <yzong@cmu.edu>
 *
 */
public class EuclideanSequential {

  /**
   * Main function for sequential K-Means for 2D points.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   */
  public static void main(String[] args) {
    String InputFile = "";
    double Epsilon = 0;
    int K = 0;
    List<Double> xList = new ArrayList<Double>();
    List<Double> yList = new ArrayList<Double>();
    /* Parse command-line arguments. */
    try {
      InputFile = args[0];
      K = Integer.parseInt(args[1]);
      Epsilon = Double.parseDouble(args[2]);
    } catch (Exception e) {
      System.out.printf("Usage: java com.yzong.dsf14.openMPIKMeans.euclidean.EuclideanSequential ");
      System.out.printf("<InputFile> <#Clusters> <Threshold>\n");
      System.exit(1);
    }

    /* Load dataset from input file. */
    BufferedReader in;
    try {
      in = new BufferedReader(new FileReader(InputFile));
      while (in.ready()) {
        String[] s = in.readLine().split(",");
        xList.add(Double.parseDouble(s[0]));
        yList.add(Double.parseDouble(s[1]));
      }
      in.close();
    } catch (IOException e) {
      System.out.printf("Exception while reading input file -- %s\n", e.getMessage());
      System.exit(1);
    }
    int N = xList.size();
    double X[] = new double[N];
    double Y[] = new double[N];
    for (int i = 0; i < N; i++) {
      X[i] = xList.get(i);
      Y[i] = xList.get(i);
    }
    System.out.printf("Successfully loaded %d data points!\n", xList.size());

    /* Pick K random points as initial centers. Initialize arrays. */
    // Rule out trivial case.
    if (K > N) {
      System.out.println("Number of clusters greater than data size!");
      System.out.println("Each cluster consists of one data entry; remaining clusters empty.");
      System.exit(0);
    }
    Set<Integer> subsetIdx = new HashSet<Integer>();
    while (subsetIdx.size() < K) {
      subsetIdx.add((int) (Math.random() * N));
    }
    double MXold[] = new double[K];
    double MYold[] = new double[K];
    double MX[] = new double[K];
    double MY[] = new double[K];
    int counter = 0;
    for (int s : subsetIdx) {
      MXold[counter] = X[s];
      MYold[counter] = X[s];
      counter++;
    }

    /* Start K-Means iterations. */
    boolean B[][] = new boolean[K][N]; // True iff Element n is associated with Center k
    double D[][] = new double[K][N]; // Distance between Element n and Center k
    while (true) {
      /* Calculate Euclidean distances. */
      for (int cluster = 0; cluster < K; cluster++) {
        // Parallelizable
        for (int elem = 0; elem < N; elem++) {
          D[cluster][elem] =
              Math.sqrt(Math.pow(X[elem] - MXold[cluster], 2)
                  + Math.pow(Y[elem] - MYold[cluster], 2));
        }
      }
      /* Re-associate elements with centers. */
      // Parallelizable
      for (int elem = 0; elem < N; elem++) {
        double minDist = Double.MAX_VALUE;
        int minIndex = -1;
        for (int cluster = 0; cluster < K; cluster++) {
          if (D[cluster][elem] < minDist) {
            minDist = D[cluster][elem];
            minIndex = cluster;
          }
        }
        for (int cluster = 0; cluster < K; cluster++) {
          B[cluster][elem] = (cluster == minIndex);
        }
      }
      /* Re-calculate center coordinates. */
      for (int cluster = 0; cluster < K; cluster++) {
        // Parallelizable
        int elemCount = 0;
        double XTotal = 0;
        double YTotal = 0;
        for (int elem = 0; elem < N; elem++) {
          if (B[cluster][elem]) {
            elemCount++;
            XTotal += X[elem];
            YTotal += Y[elem];
          }
        }
        MX[cluster] = XTotal / elemCount;
        MY[cluster] = YTotal / elemCount;
      }
      /* Check if centers converge. */
      boolean finish = true;
      for (int i = 0; i < K; i++) {
        if (Math.sqrt(Math.pow(MX[i] - MXold[i], 2) + Math.pow(MY[i] - MYold[i], 2)) > Epsilon) {
          finish = false;
          break;
        }
      }
      /* If all centers converge, complete! */
      if (finish) {
        break;
      }
      /* Update center coordinates. */
      MXold = MX.clone();
      MYold = MY.clone();
    }

    /* Output result to user */
    try {
      String OutputFile = InputFile + ".out";
      System.out.printf("Writing clustering result to `%s`...", OutputFile);
      PrintWriter writer = new PrintWriter(OutputFile, "UTF-8");
      writer.println("Following are the cluster centers:");
      for (int cluster = 0; cluster < K; cluster++) {
        writer.printf("Cluster %d: %f, %f\n", cluster, MX[cluster], MY[cluster]);
      }
      writer.println("Following are the cluster association for each element:");
      for (int elem = 0; elem < N; elem++) {
        for (int cluster = 0; cluster < K; cluster++) {
          if (B[cluster][elem]) {
            writer.printf("%d\n", cluster);
          }
        }
      }
      writer.println("--- End of Output ---");
      writer.flush();
      writer.close();
      System.out.printf("Done!\n");
    } catch (IOException e) {
      System.out.printf("\nFailed to write to output file -- %s\n", e.getMessage());
      System.exit(1);
    }
  }
}

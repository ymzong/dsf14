package com.yzong.dsf14.openMPIKMeans.strandDataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entry point for sequential K-Means algorithm for DNA strands.
 * 
 * @author Yiming Zong <yzong@cmu.edu>
 *
 */
public class DNASequential {

  /**
   * Main function for sequential K-Means for DNA strands.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   */
  public static void main(String[] args) {
    String InputFile = "";
    int Epsilon = 0;
    int K = 0;
    int L = -1;
    List<String> dnaList = new ArrayList<String>();
    /* Parse command-line arguments. */
    try {
      InputFile = args[0];
      K = Integer.parseInt(args[1]);
      Epsilon = Math.max(1, Integer.parseInt(args[2]));
    } catch (Exception e) {
      System.out.printf("Usage: java com.yzong.dsf14.openMPIKMeans.strandDataset.DNASequential ");
      System.out.printf("<InputFile> <#Clusters> <Threshold>\n");
      System.exit(1);
    }

    /* Load dataset from input file. */
    BufferedReader in;
    try {
      in = new BufferedReader(new FileReader(InputFile));
      while (in.ready()) {
        dnaList.add(in.readLine());
      }
      in.close();
    } catch (IOException e) {
      System.out.printf("Exception while reading input file -- %s\n", e.getMessage());
      System.exit(1);
    }
    int N = dnaList.size();
    if (N == 0) {
      System.out.println("Empty dataset!");
      System.exit(0);
    }
    String DNAs[] = new String[N];
    for (int i = 0; i < N; i++) {
      DNAs[i] = dnaList.get(i);
    }
    L = DNAs[0].length();
    System.out.printf("Successfully loaded %d data points!\n", L);

    /* Pick K random strands as initial centers. Initialize arrays. */
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
    String CentroidsOld[] = new String[K];
    String Centroids[] = new String[K];
    int counter = 0;
    for (int s : subsetIdx) {
      CentroidsOld[counter] = DNAs[s];
      counter++;
    }

    /* Start K-Means iterations. */
    int assoc[]; // Center association of each element
    int numD[]; // Number of strands in each cluster
    while (true) {
      assoc = new int[N];
      numD = new int[K];
      int ATotal[][] = new int[K][L];
      int CTotal[][] = new int[K][L];
      int GTotal[][] = new int[K][L];
      int TTotal[][] = new int[K][L];
      /* For each strand, calculate its similarity to all centers to re-associate it. */
      for (int elem = 0; elem < N; elem++) {
        int maxSimilarity = 0;
        int maxIndex = -1;
        for (int cluster = 0; cluster < K; cluster++) {
          int s = getSimilarity(CentroidsOld[cluster], DNAs[elem]);
          if (s > maxSimilarity) {
            maxSimilarity = s;
            maxIndex = cluster;
          }
        }
        /* Update data structures with statistics. */
        assoc[elem] = maxIndex;
        numD[maxIndex]++;
        for (int l = 0; l < L; l++) {
          switch (DNAs[elem].charAt(l)) {
            case 'A':
              ATotal[maxIndex][l]++;
              break;
            case 'C':
              CTotal[maxIndex][l]++;
              break;
            case 'G':
              GTotal[maxIndex][l]++;
              break;
            case 'T':
              TTotal[maxIndex][l]++;
              break;
          }
        }
      }
      /* Re-calculate new centroid strands. */
      for (int cluster = 0; cluster < K; cluster++) {
        /* Rare case: no data points associated with cluster. Center set to random point. */
        if (numD[cluster] == 0) {
          int indx = (int) (Math.random() * N);
          Centroids[cluster] = DNAs[indx];
          System.out.printf("Warning: no points assigned to cluster %d!\n", cluster);
        } else {
          String c = "";
          for (int l = 0; l < L; l++) {
            int[] tmpArr =
                {ATotal[cluster][l], CTotal[cluster][l], GTotal[cluster][l], TTotal[cluster][l]};
            int[] tmpArrSorted = Arrays.copyOf(tmpArr, tmpArr.length);
            Arrays.sort(tmpArrSorted);
            if (tmpArr[0] == tmpArrSorted[tmpArr.length - 1]) {
              c += "A";
            } else if (tmpArr[1] == tmpArrSorted[tmpArr.length - 1]) {
              c += "C";
            } else if (tmpArr[2] == tmpArrSorted[tmpArr.length - 1]) {
              c += "G";
            } else if (tmpArr[3] == tmpArrSorted[tmpArr.length - 1]) {
              c += "T";
            }
          }
          Centroids[cluster] = c;
        }
      }
      /* Check if centers converge. */
      boolean converge = true;
      for (int i = 0; i < K; i++) {
        if (L - getSimilarity(Centroids[i], CentroidsOld[i]) >= Epsilon) {
          converge = false;
          break;
        }
      }
      /* If all centers converge, complete! */
      if (converge) {
        break;
      }

      /* Update center strands. */
      CentroidsOld = Centroids.clone();
    }

    /* Output result to user */
    try {
      String OutputFile = InputFile + ".out";
      System.out.printf("Writing clustering result to `%s`...", OutputFile);
      PrintWriter writer = new PrintWriter(OutputFile, "UTF-8");
      writer.println("Following are the cluster centers:");
      for (int cluster = 0; cluster < K; cluster++) {
        writer.printf("Cluster %d: %s\n", cluster, Centroids[cluster]);
      }
      writer.println("Following are the cluster association for each element:");
      for (int elem = 0; elem < N; elem++) {
        writer.printf("%d\n", assoc[elem]);
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

  private static int getSimilarity(String dna1, String dna2) {
    int score = dna1.length();
    for (int i = 0; i < dna1.length(); i++) {
      if (dna1.indexOf(i) != dna2.indexOf(i)) {
        score--;
      }
    }
    return score;
  }
}

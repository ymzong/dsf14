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

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * Entry point for parallel K-Means algorithm for DNA strands.
 * 
 * @author Yiming Zong <yzong@cmu.edu>
 *
 */
public class DNAParallel {

  private final static int MASTER_ID = 0;
  private final static int CENTROIDS_PKG = 15440;
  private final static int ASSOC_PKG = 15441;
  private final static int NUMD_PKG = 15442;
  private final static int ATOTAL_PKG = 15443;
  private final static int CTOTAL_PKG = 15444;
  private final static int GTOTAL_PKG = 15445;
  private final static int TTOTAL_PKG = 15446;
  private final static int CONT_PKG = 15447;

  private String InputFile = "";
  private int Epsilon = 0;
  private int K = 0;
  private int L = -1;
  private int N;
  private String DNAs[];
  private String Centroids[];
  private String CentroidsOld[];
  private int assoc[];
  private int numD[];
  private int ATotal[][], CTotal[][], GTotal[][], TTotal[][];

  private int MyRank;
  private int UnivSize;
  private int StartIdx;
  private int EndIdx;
  private int ChunkSize;

  /**
   * Main function for sequential K-Means for DNA strands.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   */
  public static void main(String[] args) throws MPIException {
    MPI.Init(args);
    DNAParallel ds = new DNAParallel();
    ds.run(args);
    MPI.Finalize();
    return;
  }

  /**
   * Runs K-means sequential algorithm on DNA strands.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   */
  public void run(String[] args) {
    /* All: Load MPI-related constants. */
    MyRank = MPI.COMM_WORLD.Rank();
    UnivSize = MPI.COMM_WORLD.Size();
    if (UnivSize < 2) {
      if (MyRank == MASTER_ID) {
        System.out.println("Only one MPI instance found! Use sequential version instead.");
      }
      MPI.Finalize();
      System.exit(0);
    }
    if (MyRank == MASTER_ID) {
      System.out.printf("OpenMPI Session Started! Universe Size: %d\n", UnivSize);
    }

    /* All: Processes input parameters and data. */
    extractInput(args);
    ChunkSize = (int) Math.ceil(((double) N) / (UnivSize - 1));
    StartIdx = ChunkSize * (MyRank - 1);
    EndIdx = Math.min(ChunkSize * MyRank, N);

    /* All: Rule out trivial case. */
    if (K > N) {
      if (MyRank == MASTER_ID) {
        System.out.println("Number of clusters greater than data size!");
        System.out.println("Each cluster consists of one data entry; remaining clusters empty.");
      }
      MPI.Finalize();
      System.exit(0);
    }

    /* All: Initialize arrays. */
    CentroidsOld = new String[K];
    Centroids = new String[K];

    /* Master: Pick K random points as initial ctrs. */
    if (MyRank == MASTER_ID) {
      initCtrs();
      System.out.println("Initialized centers. Starting iterations...");
    }

    /* All: Start K-Means iterations. */
    while (true) {
      /* Broadcast all CentroidsOld cluster strands. */
      broadcastCentroidsOld();
      /* Initialize data structures with statistics. */
      assoc = new int[N];
      numD = new int[K];
      ATotal = new int[K][L];
      CTotal = new int[K][L];
      GTotal = new int[K][L];
      TTotal = new int[K][L];
      /* Worker: Re-associate strands in the interval [StartIdx, EndIdx). */
      if (MyRank != MASTER_ID) {
        /* Calculate new associations. */
        for (int elem = StartIdx; elem < EndIdx; elem++) {
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
        /* Send result to Master */
        MPI.COMM_WORLD.Issend(numD, 0, K, MPI.INT, MASTER_ID, NUMD_PKG);
        MPI.COMM_WORLD.Issend(assoc, StartIdx, EndIdx - StartIdx, MPI.INT, MASTER_ID, ASSOC_PKG);
        MPI.COMM_WORLD.Issend(ATotal, 0, K, MPI.OBJECT, MASTER_ID, ATOTAL_PKG);
        MPI.COMM_WORLD.Issend(CTotal, 0, K, MPI.OBJECT, MASTER_ID, CTOTAL_PKG);
        MPI.COMM_WORLD.Issend(GTotal, 0, K, MPI.OBJECT, MASTER_ID, GTOTAL_PKG);
        MPI.COMM_WORLD.Issend(TTotal, 0, K, MPI.OBJECT, MASTER_ID, TTOTAL_PKG);
        /* Block till Master informs convergence or not. */
        boolean convg[] = new boolean[1];
        MPI.COMM_WORLD.Recv(convg, 0, 1, MPI.BOOLEAN, MASTER_ID, CONT_PKG);
        if (convg[0]) {
          break; // If Master finds convergence, Worker instance breaks out of while loop.
        }
      }
      /* Master: Gather the results from Workers. Calculate new centers. */
      else {
        int totalBuffer[][] = new int[K][L];
        int intBuffer[] = new int[Math.max(ChunkSize, K)];
        for (int count = 1; count < UnivSize; count++) {
          // Process numD arrays.
          MPI.COMM_WORLD.Recv(intBuffer, 0, K, MPI.INT, MPI.ANY_SOURCE, NUMD_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            numD[cluster] += intBuffer[cluster];
          }
          // Stick up associations in assoc sub-arrays.
          Status s =
              MPI.COMM_WORLD.Recv(intBuffer, 0, ChunkSize, MPI.INT, MPI.ANY_SOURCE, ASSOC_PKG);
          int start = ChunkSize * (s.source - 1);
          int end = Math.min(ChunkSize * s.source, N);
          for (int elem = start; elem < end; elem++) {
            assoc[elem] = intBuffer[elem - start];
          }
          // Process ATotal, CTotal, GTotal, and TTotal arrays.
          MPI.COMM_WORLD.Recv(totalBuffer, 0, K, MPI.OBJECT, MPI.ANY_SOURCE, ATOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            for (int l = 0; l < L; l++) {
              ATotal[cluster][l] += totalBuffer[cluster][l];
            }
          }
          MPI.COMM_WORLD.Recv(totalBuffer, 0, K, MPI.OBJECT, MPI.ANY_SOURCE, CTOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            for (int l = 0; l < L; l++) {
              CTotal[cluster][l] += totalBuffer[cluster][l];
            }
          }
          MPI.COMM_WORLD.Recv(totalBuffer, 0, K, MPI.OBJECT, MPI.ANY_SOURCE, GTOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            for (int l = 0; l < L; l++) {
              GTotal[cluster][l] += totalBuffer[cluster][l];
            }
          }
          MPI.COMM_WORLD.Recv(totalBuffer, 0, K, MPI.OBJECT, MPI.ANY_SOURCE, TTOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            for (int l = 0; l < L; l++) {
              TTotal[cluster][l] += totalBuffer[cluster][l];
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
            Centroids[cluster] = updatedCenter(cluster);
          }
        }
        /* Check if centers converge. */
        boolean converge[] = new boolean[1];
        converge[0] = true;
        for (int i = 0; i < K; i++) {
          if (L - getSimilarity(Centroids[i], CentroidsOld[i]) >= Epsilon) {
            converge[0] = false;
            break;
          }
        }
        /* Tell children to about convergence. */
        for (int rank = 1; rank < UnivSize; rank++) {
          MPI.COMM_WORLD.Issend(converge, 0, 1, MPI.BOOLEAN, rank, CONT_PKG);
        }
        /* If all centers converge, complete! */
        if (converge[0]) {
          break;
        }
        /* Update center strands. */
        else {
          CentroidsOld = Centroids.clone();
        }
      }
    }

    /* Output result to user */
    outputResult();
  }

  /**
   * Initializes centers with random data entries. (Used by Master)
   */
  private void initCtrs() {
    Set<Integer> subsetIdx = new HashSet<Integer>();
    while (subsetIdx.size() < K) {
      subsetIdx.add((int) (Math.random() * N));
    }

    int counter = 0;
    for (int s : subsetIdx) {
      CentroidsOld[counter] = DNAs[s];
      counter++;
    }
  }

  /**
   * Loads user parameters and data.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   */
  private void extractInput(String[] args) {
    /* All: Parse command-line arguments. */
    try {
      InputFile = args[0];
      K = Integer.parseInt(args[1]);
      Epsilon = Math.max(1, Integer.parseInt(args[2]));
    } catch (Exception e) {
      /* If parameters invalid, Master prints out message. All quit. */
      if (MyRank == MASTER_ID) {
        System.out.printf("Arguments: <InputFile> <#Clusters> <Threshold>\n");
      }
      MPI.Finalize();
      System.exit(0);
    }

    /* All: Load dataset from input file. */
    BufferedReader in;
    List<String> dnaList = new ArrayList<String>();
    try {
      in = new BufferedReader(new FileReader(InputFile));
      while (in.ready()) {
        dnaList.add(in.readLine());
      }
      in.close();
    } catch (IOException e) {
      /* Should I/O Exception occur, Master prints out message. All quit. */
      if (MyRank == MASTER_ID) {
        System.out.printf("Exception while reading input file -- %s\n", e.getMessage());
      }
      MPI.Finalize();
      System.exit(0);
    }
    N = dnaList.size();
    // Eliminates trivial case where dataset is empty.
    if (N == 0) {
      if (MyRank == MASTER_ID) {
        System.out.println("Empty dataset!");
      }
      MPI.Finalize();
      System.exit(0);
    }
    DNAs = new String[N];
    for (int i = 0; i < N; i++) {
      DNAs[i] = dnaList.get(i);
    }
    L = DNAs[0].length();
    if (MyRank == MASTER_ID) {
      System.out.printf("Successfully loaded %d data points!\n", N);
    }
  }

  /**
   * Calculates new center based on statistics.
   * 
   * @param cluster Index of cluster to update
   */
  private String updatedCenter(int cluster) {
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
    return c;
  }

  /**
   * Outputs final result to local file.
   */
  private void outputResult() {
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

  /**
   * Calculates the degree of similarity between two DNA strands.
   * 
   * @param dna1 First DNA strand
   * @param dna2 Second DNA strand
   * @return Similarity between two strands, i.e. number of identical bits
   */
  private static int getSimilarity(String dna1, String dna2) {
    int score = dna1.length();
    for (int i = 0; i < dna1.length(); i++) {
      if (dna1.indexOf(i) != dna2.indexOf(i)) {
        score--;
      }
    }
    return score;
  }

  /**
   * Broadcasts current value of CentroidsOld from Master. (Used by All)
   * 
   * @throws MPIException MPI-related exceptions during execution.
   */
  private void broadcastCentroidsOld() throws MPIException {
    if (MyRank == MASTER_ID) {
      for (int rank = 1; rank < UnivSize; rank++) {
        MPI.COMM_WORLD.Send(CentroidsOld, 0, K, MPI.OBJECT, rank, CENTROIDS_PKG);
      }
    }
    /* Workers: Receive initial centers from Master. */
    else {
      MPI.COMM_WORLD.Recv(CentroidsOld, 0, K, MPI.OBJECT, MASTER_ID, CENTROIDS_PKG);
    }
  }

}

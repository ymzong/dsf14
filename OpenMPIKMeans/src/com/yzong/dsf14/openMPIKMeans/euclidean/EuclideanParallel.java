package com.yzong.dsf14.openMPIKMeans.euclidean;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

/**
 * Entry point for parallel K-Means algorithm in 2D Euclidean space.
 * 
 * @author Yiming Zong <yzong@cmu.edu>
 *
 */
public class EuclideanParallel {

  private final static int MASTER_ID = 0;
  private final static int CLUSTERX_PKG = 15440;
  private final static int CLUSTERY_PKG = 15441;
  private final static int NUMD_PKG = 15442;
  private final static int ASSOC_PKG = 15443;
  private final static int XTOTAL_PKG = 15444;
  private final static int YTOTAL_PKG = 15445;
  private final static int CONT_PKG = 15446;

  private String InputFile;
  private double Epsilon;
  private double X[];
  private double Y[];
  private double MXold[];
  private double MYold[];
  private double MX[];
  private double MY[];
  private double XTotal[];
  private double YTotal[];
  private int N;
  private int K;

  private int MyRank;
  private int UnivSize;
  private int StartIdx;
  private int EndIdx;

  /**
   * Main function for parallel K-Means for 2D points.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   * @throws MPIException MPI-related exceptions during execution.
   */
  public static void main(String[] args) throws MPIException {
    MPI.Init(args);
    EuclideanParallel ep = new EuclideanParallel();
    ep.run(args);
    MPI.Finalize();
    return;
  }

  /**
   * Runs K-Means parallel algorithm.
   * 
   * @param args <tt>InputFileName</tt>, <tt>NumberOfClusters</tt>, <tt>TerminationThreshold</tt>.
   * @throws MPIException MPI-related exceptions during execution.
   */
  public void run(String[] args) throws MPIException {
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
    /* All: Load input parameters and data from user. */
    extractInput(args);
    int chunkSize = (int) Math.ceil(((double) N) / (UnivSize - 1));
    StartIdx = chunkSize * (MyRank - 1);
    EndIdx = Math.min(chunkSize * MyRank, N);

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
    MXold = new double[K];
    MYold = new double[K];
    MX = new double[K];
    MY = new double[K];
    /* Master: Pick K random points as initial ctrs. */
    if (MyRank == MASTER_ID) {
      initCtrs();
      System.out.println("Initialized centers. Starting iterations...");
    }

    /* All: Start K-Means iterations. */
    int numD[]; // Number of elements associated with each center
    int assoc[]; // Center association of each element
    while (true) {
      /* Broadcast all MXold, MYold cluster coordinates. */
      broadcastMXYold();
      /* Initialize data structures for calculation. */
      numD = new int[K];
      assoc = new int[N];
      XTotal = new double[K];
      YTotal = new double[K];
      /* Worker: Re-associate nodes in the interval [StartIdx, EndIdx). */
      if (MyRank != MASTER_ID) {
        /* Calculate new associations. */
        for (int elem = StartIdx; elem < EndIdx; elem++) {
          double minDist = Double.MAX_VALUE;
          int minIndex = -1;
          for (int cluster = 0; cluster < K; cluster++) {
            double d =
                Math.sqrt(Math.pow(X[elem] - MXold[cluster], 2)
                    + Math.pow(Y[elem] - MYold[cluster], 2));
            if (d < minDist) {
              minDist = d;
              minIndex = cluster;
            }
          }
          numD[minIndex]++;
          assoc[elem] = minIndex;
          XTotal[minIndex] += X[elem];
          YTotal[minIndex] += Y[elem];
        }
        /* Send result to Master */
        MPI.COMM_WORLD.Issend(numD, 0, K, MPI.INT, MASTER_ID, NUMD_PKG);
        MPI.COMM_WORLD.Issend(assoc, StartIdx, EndIdx - StartIdx, MPI.INT, MASTER_ID, ASSOC_PKG);
        MPI.COMM_WORLD.Issend(XTotal, 0, K, MPI.DOUBLE, MASTER_ID, XTOTAL_PKG);
        MPI.COMM_WORLD.Issend(YTotal, 0, K, MPI.DOUBLE, MASTER_ID, YTOTAL_PKG);
        /* Block till Master informs convergence or not. */
        boolean convg[] = new boolean[1];
        MPI.COMM_WORLD.Recv(convg, 0, 1, MPI.BOOLEAN, MASTER_ID, CONT_PKG);
        if (convg[0]) {
          break; // If Master finds convergence, Worker instance breaks out of while loop.
        }
      }
      /* Master: Gather the results from Workers. */
      else {
        double doubleBuffer[] = new double[K];
        int intBuffer[] = new int[Math.max(chunkSize, K)];
        for (int count = 1; count < UnivSize; count++) {
          // Process numD arrays.
          MPI.COMM_WORLD.Recv(intBuffer, 0, K, MPI.INT, MPI.ANY_SOURCE, NUMD_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            numD[cluster] += intBuffer[cluster];
          }
          // Process XTotal and YTotal arrays.
          MPI.COMM_WORLD.Recv(doubleBuffer, 0, K, MPI.DOUBLE, MPI.ANY_SOURCE, XTOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            XTotal[cluster] += doubleBuffer[cluster];
          }
          MPI.COMM_WORLD.Recv(doubleBuffer, 0, K, MPI.DOUBLE, MPI.ANY_SOURCE, YTOTAL_PKG);
          for (int cluster = 0; cluster < K; cluster++) {
            YTotal[cluster] += doubleBuffer[cluster];
          }
          // Stick up associations in assoc sub-arrays.
          Status s =
              MPI.COMM_WORLD.Recv(intBuffer, 0, chunkSize, MPI.INT, MPI.ANY_SOURCE, ASSOC_PKG);
          int start = chunkSize * (s.source - 1);
          int end = Math.min(chunkSize * s.source, N);
          for (int elem = start; elem < end; elem++) {
            assoc[elem] = intBuffer[elem - start];
          }
        }
        /* Re-calculate center coordinates. */
        for (int cluster = 0; cluster < K; cluster++) {
          /* Rare case: no data points associated with cluster. Center set to random point. */
          if (numD[cluster] == 0) {
            int indx = (int) (Math.random() * N);
            MX[cluster] = X[indx];
            MY[cluster] = Y[indx];
            System.out.printf("Warning: no points assigned to cluster %d!\n", cluster);
          } else {
            MX[cluster] = XTotal[cluster] / numD[cluster];
            MY[cluster] = YTotal[cluster] / numD[cluster];
          }
        }
        /* Check if centers converge. */
        boolean converge[] = new boolean[1];
        converge[0] = true;
        for (int i = 0; i < K; i++) {
          if (Math.sqrt(Math.pow(MX[i] - MXold[i], 2) + Math.pow(MY[i] - MYold[i], 2)) > Epsilon) {
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
        /* Otherwise, update center coordinates. */
        else {
          MXold = MX.clone();
          MYold = MY.clone();
        }
      }
    }
    /* Writes result to local file. */
    if (MyRank == MASTER_ID) {
      outputResult(assoc);
    }
    return;
  }

  /**
   * Outputs final result to local file. (Used by Master)
   * 
   * @param assoc Final association of each data entry with its cluster
   */
  private void outputResult(int[] assoc) {
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
        writer.printf("%d\n", assoc[elem]);
      }
      writer.println("--- End of Output ---");
      writer.flush();
      writer.close();
      System.out.printf("Done!\n");
    } catch (IOException e) {
      System.out.printf("\nFailed to write to output file -- %s\n", e.getMessage());
      return;
    }
  }

  /**
   * Broadcasts current value of MXold and MYold from master. (Used by All)
   * 
   * @throws MPIException MPI-related exceptions during execution.
   */
  private void broadcastMXYold() throws MPIException {
    if (MyRank == MASTER_ID) {
      for (int rank = 1; rank < UnivSize; rank++) {
        MPI.COMM_WORLD.Send(MXold, 0, K, MPI.DOUBLE, rank, CLUSTERX_PKG);
        MPI.COMM_WORLD.Send(MYold, 0, K, MPI.DOUBLE, rank, CLUSTERY_PKG);
      }
    }
    /* Workers: Receive initial centers from Master. */
    else {
      MPI.COMM_WORLD.Recv(MXold, 0, K, MPI.DOUBLE, MASTER_ID, CLUSTERX_PKG);
      MPI.COMM_WORLD.Recv(MYold, 0, K, MPI.DOUBLE, MASTER_ID, CLUSTERY_PKG);
    }
  }

  /**
   * Loads input parameters and data from user. (Used by All)
   * 
   * @param args Command-line arguments from user
   * @throws MPIException MPI-related exceptions during execution.
   */
  private void extractInput(String[] args) throws MPIException {
    /* All: Parse command-line arguments. */
    try {
      InputFile = args[0];
      K = Integer.parseInt(args[1]);
      Epsilon = Double.parseDouble(args[2]);
    } catch (Exception e) {
      /* If parameters invalid, Master prints out message. All quit. */
      if (MyRank == MASTER_ID) {
        System.out.printf("Arguments: <InputFile> <#Clusters> <Threshold>\n");
      }
      MPI.Finalize();
      System.exit(0);
    }

    /* All: Load dataset from input file. */
    List<Double> xList = new ArrayList<Double>();
    List<Double> yList = new ArrayList<Double>();
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
      /* Should I/O Exception occur, Master prints out message. All quit. */
      if (MyRank == MASTER_ID) {
        System.out.printf("Exception while reading input file -- %s\n", e.getMessage());
      }
      MPI.Finalize();
      System.exit(0);
    }
    N = xList.size();
    X = new double[N];
    Y = new double[N];
    for (int i = 0; i < N; i++) {
      X[i] = xList.get(i);
      Y[i] = yList.get(i);
    }
    if (MyRank == MASTER_ID) {
      System.out.printf("Successfully loaded %d data points!\n", xList.size());
    }
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
      MXold[counter] = X[s];
      MYold[counter] = Y[s];
      counter++;
    }
  }
}

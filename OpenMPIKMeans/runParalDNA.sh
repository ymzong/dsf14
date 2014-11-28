cd src;
time mpirun --hostfile ../machines.txt -np 16 java com.yzong.dsf14.openMPIKMeans.strandDataset.DNAParallel ../input/randDNA.csv 15 2;
cd ..;


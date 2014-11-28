cd src;
time mpirun --hostfile ../machines.txt -np 16 java com.yzong.dsf14.openMPIKMeans.euclidean.EuclideanParallel ../input/rand2D.csv 15 0.0000000001;
cd ..;


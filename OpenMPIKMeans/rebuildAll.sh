echo -n Re-building in progress...

# Clean up existing class files
rm -f `find src -name "*.class"`

# Compile Sequential K-Means on 2D
javac -g src/com/yzong/dsf14/openMPIKMeans/euclidean/EuclideanSequential.java

# Compile Parallel K-Means on 2D
mpijavac -g src/com/yzong/dsf14/openMPIKMeans/euclidean/EuclideanParallel.java

# Compile Sequential K-Means on DNA
javac -g src/com/yzong/dsf14/openMPIKMeans/strandDataset/DNASequential.java

# Compile Parallel K-Means on DNA
mpijavac -g src/com/yzong/dsf14/openMPIKMeans/strandDataset/DNAParallel.java

echo Done!


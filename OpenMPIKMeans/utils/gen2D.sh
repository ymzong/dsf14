#!/bin/sh

#Number of Points
echo -n "Number of 2D points [10]: "
read b
if [ "$b" = '' ]; then
  b=10
fi

#Number of Cluster
echo -n "Number of clusters [2]: "
read k
if [ "$k" = '' ]; then
  k=2
fi

echo ***GENERATING $b INPUT POINTS EACH IN $k CLUSTERS***
mkdir -p ../input
python ./randClusterGen/gen2D.py -c $k -p $b -o "../input/rand2D.csv"
echo Dataset written to ../input/cluster.csv!


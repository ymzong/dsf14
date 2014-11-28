#!/bin/sh

#Number of DNA strands
echo -n "Number of DNA strands [10]: "
read b
if [ "$b" = '' ]; then
  b=10
fi

#Length of DNA strands
echo -n "Length of DNA strands [100]: "
read l
if [ "$l" = '' ]; then
  l=100
fi

#Number of Cluster
echo -n "Number of clusters [2]: "
read k
if [ "$k" = '' ]; then
  k=2
fi

echo ***GENERATING $b DNA STRANDS EACH IN $k CLUSTERS***
mkdir -p ../input
python ./randClusterGen/genDNA.py -c $k -p $b -o "../input/randDNA.csv" -l $l
echo Dataset written to ../input/randDNA.csv!


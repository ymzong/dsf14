import sys
import csv
import numpy
import getopt
import math
from random import randrange
from scipy import stats

def usage():
    print '$> python generaterawdata.py <required args>\n' + \
            '\t-c <#>\t\tNumber of clusters to generate\n' + \
            '\t-p <#>\t\tNumber of points per cluster\n' + \
            '\t-o <file>\tFilename for the output of the raw data\n' + \
            '\t-l [#]\t\tLength of each DNA strand\n'  

def similarity(dna1, dna2):
    '''
    Takes two DNA strands of same size and computes their similarity value.
    '''
    n = len(dna1)
    for i in xrange(n):
        if dna1[i] != dna2[i]:
            n -= 1
    return n

def tooClose(dna, dnas, maxSimilarity):
    '''
    Computes the similarity between the given dna and all others in the
    list, and if any dna in the list is more similar than maxSimilarity,
    this method returns true.
    '''
    for d in dnas:
        if similarity(dna, d) > maxSimilarity:
            return True
    return False

def handleArgs(args):
    '''
    Processes user's input arguments.
    '''
    numClusters = -1
    numDNAs = -1
    output = None
    dnaLength = -1

    try:
        optlist, args = getopt.getopt(args[1:], 'c:p:l:o:')
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    for key, val in optlist:
        # first, the required arguments
        if   key == '-c':
            numClusters = int(val)
        elif key == '-p':
            numDNAs = int(val)
        elif key == '-o':
            output = val
        elif key == '-l':
            dnaLength = int(val)

    # check required arguments were inputted  
    if numClusters < 0 or numDNAs < 0 or output is None or dnaLength <= 0:
        usage()
        sys.exit()
    return (numClusters, numDNAs, output, dnaLength)

def genDNA(length):
    dna = ""
    for i in xrange(length):
        dna += DNA_BITS[randrange(4)]
    return dna

def genDNAfromCentroid(d, prob):
    dna = ""
    prob = float(prob)
    drift = numpy.arange(4)
    pk = (1-prob, prob/3, prob/3, prob/3)
    driftRV = stats.rv_discrete(name='drift', values=(drift, pk))
    for i in xrange(len(d)):
        oldIdx = DNA_BITS.index(d[i])
        newIdx = oldIdx + (driftRV.rvs(size = 1))[0]
        dna += DNA_BITS[newIdx % 4]
    return dna

DNA_BITS = ("A", "C", "G", "T")
# start by reading the command line
numClusters, numStrands, output, dnaLength = handleArgs(sys.argv)

writer = csv.writer(open(output, "w"))

# step 1: generate each DNA centroid
centroidDNAs = []
maxSimilarity = dnaLength * 0.5
for i in range(0, numClusters):
    newDNA = genDNA(dnaLength)
    # is it far enough from the others?
    while (tooClose(newDNA, centroidDNAs, maxSimilarity)):
        newDNA = genDNA(dnaLength)
    centroidDNAs.append(newDNA)

# step 2: generate the DNAs for each centroid
points = []
minDiffProbability = 0.05
maxDiffProbability = 0.2
for i in range(0, numClusters):
    diffProbability = numpy.random.uniform(minDiffProbability, maxDiffProbability)
    cluster = centroidDNAs[i]
    for j in range(0, numStrands):
        # generate a DNA strand with specified probability of difference
        dna = genDNAfromCentroid(cluster, diffProbability)
        # write the DNA out
        writer.writerow([dna])


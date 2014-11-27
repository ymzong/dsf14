% This script visualizes the 2D input data from CSV
% Replace with input file name below
file = '/home/jimmy/workspace/dsf14/OpenMPIKMeans/input/rand2D';
M = csvread(strcat(file, '.csv'));
X = M(:,1);
Y = M(:,2);
scatter(X, Y, 9, 'filled')
set(0,'ShowHiddenHandles','on')
print('-depsc2', '-painters', strcat(file, '.eps'));


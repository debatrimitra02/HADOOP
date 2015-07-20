I took 8 features to run the K-means algorithm

Here is the sample output I got after running with k=10 
–

[0.98, 514.5, 294.0, 110.25, 7.0, 5.0, 0.1, 2.0]
[0.9, 563.5, 318.5, 122.5, 7.0, 2.0, 0.25, 2.0]
[0.9, 563.5, 318.5, 122.5, 7.0, 4.0, 0.1, 1.0]
[0.86, 588.0, 294.0, 147.0, 7.0, 4.0, 0.25, 4.0]
[0.86, 588.0, 294.0, 147.0, 7.0, 2.0, 0.1, 1.0]
[0.82, 612.5, 318.5, 147.0, 7.0, 3.0, 0.4, 5.0]
[0.76, 661.5, 416.5, 122.5, 7.0, 4.0, 0.25, 5.0]
[0.74, 686.0, 245.0, 220.5, 3.5, 3.0, 0.1, 2.0]
[0.71, 710.5, 269.5, 220.5, 3.5, 4.0, 0.1, 5.0]
[0.64, 784.0, 343.0, 220.5, 3.5, 2.0, 0.1, 2.0]











Step-1
We insert a column before the 8 Feature columns where we give row id-s
As row 1, row 2….…as asked in the problem.

Step-2
Reading data to Hbase table.
We initialize hBase tables first to load the data

HBase Tables
create 'adjacency-matrix', 'cf'
create 'kmeans-centroids', {NAME => 'cf', VERSIONS => 10}

You can change the3 version name to different value. It just means how many versions of the hBase tables it needs to maintain. So, basically we iterate through the k-means and keep the intermediate results cached.
 



Run the Program.sh with two parameters
" Expected 2 parameters."
       "Param1: Path to data points csv file"
        "Param2: Number of clusters - K"

       "Sample: ./Program.sh /home/usr/file.csv 3"

It will automatically do everything – read data to hBase, run k-means and write output into a hBase table.



It will clean all the intermediate files on each run
 

#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters. Expected 2 parameters."
        echo "Param1: Path to data points csv file"
        echo "Param2: Number of clusters - K"

        echo
        echo "Sample: ./kmeans.sh /home/usr/file.csv 3"
        exit 1
fi

## Data Cleanup
hadoop fs -rm -r /user/debatri/output-*
hadoop fs -rmr /user/debatri/centroids.dat

## Classpath Setup for MR
export HADOOP_CLASSPATH=/usr/hdp/current/hbase-client/lib/hbase-client.jar:/usr/hdp/current/hbase-client/lib/hbase-common.jar:/usr/hdp/current/hbase-client/li
b/hbase-protocol.jar:/home/dkari/htrace-core-2.04.jar:/usr/hdp/current/hbase-client/lib/hbase-server.jar:/usr/hdp/current/hbase-client/lib/hbase-hadoop-compat
.jar:/home/debatri/high-scale-lib-1.1.1.jar

export LIBJARS=/usr/hdp/current/hbase-client/lib/hbase-client.jar,/usr/hdp/current/hbase-client/lib/hbase-common.jar,/usr/hdp/current/hbase-client/lib/hbase-p
rotocol.jar,/home/dkari/htrace-core-2.04.jar,/usr/hdp/current/hbase-client/lib/hbase-server.jar,/usr/hdp/current/hbase-client/lib/hbase-hadoop-compat.jar,/hom
e/dkari/high-scale-lib-1.1.1.jar

# Data Ingestion
# java -cp /home/debatri/KMeansClusteringMR-0.0.1.jar com.slb.analytics.hbase.ExportCsv $1
java -cp /home/debatri/KMeansClusteringMR-0.0.1-jar-with-dependencies.jar com.slb.analytics.hbase.ExportCsv $1

# Run KMeans Algorithm on data loaded in previous step
hadoop jar /home/debatri/KMeansClusteringMR-0.0.1.jar com.slb.analytics.kmeans.KMeans --libjars ${LIBJARS} /user/debatri/ /user/debatri/output $2

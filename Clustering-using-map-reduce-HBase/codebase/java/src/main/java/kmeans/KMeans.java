package kmeans;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.slb.analytics.hbase.Centroid;
import com.slb.analytics.hbase.HBaseUtil;
import com.slb.analytics.hbase.SingletonConnection;

public class KMeans {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
        String[] otherArgs = new GenericOptionsParser(configuration, args).getRemainingArgs();
        if (otherArgs.length != 3) {
            System.err.println("Usage: KMeans <in> <out> <clusters_number>");
            System.exit(2);
        }

        int centroidsNumber = Integer.parseInt(otherArgs[2]);
        configuration.setInt(Constants.CENTROID_NUMBER_ARG, centroidsNumber);
        configuration.set(Constants.INPUT_FILE_ARG, otherArgs[0]);
        configuration.set(Constants.FINAL_OUTPUT_FILE_ARG, otherArgs[0]);
        // configuration.set(Constants.OUTPUT_FILE_ARG, otherArgs[1]);

        // Get K random centroids
        // List<Double[]> centroids = Utils.createRandomCentroids(centroidsNumber);
        List<Double[]> centroids = Utils.getKRandomRows(centroidsNumber);
        
        for (Double[] centroid : centroids) {
    		StringBuilder s = new StringBuilder();
    		for (Double double1 : centroid) {
        		s = s.append(double1.toString());
                s.append(" ");
			}
    		// System.out.println("***************** Initial Centroids: " + s);
    	}
        
        System.out.println("***************** Initial Centroids:");
        String centroidsFile = Utils.getFormattedCentroids(centroids);

        // writes centroids on distributed cache
        Utils.writeCentroids(configuration, centroidsFile);
        
        boolean hasConverged = false;
        int iteration = 0;
        do {

        	// System.out.println("@@@@@@@@@@@@@@@@@@@" + Constants.OUTPUT_FILE_ARG + otherArgs[1] + "-" + iteration);
        	
        	configuration.set(Constants.OUTPUT_FILE_ARG, otherArgs[1] + "-" + iteration);

            // executes hadoop job
            if (!launchJob(configuration)) {

                // if an error has occurred stops iteration and terminates
                System.exit(1);
            }

            // reads reducer output file
            String newCentroids = Utils.readReducerOutput(configuration);
            
            System.out.println("***************** NewCentroids: " + newCentroids);

            // if the output of the reducer equals the old one
            if (centroidsFile.equals(newCentroids)) {

                // it means that the iteration is finished
                hasConverged = true;
            }
            else {

                // writes the reducers output to distributed cache
                Utils.writeCentroids(configuration, newCentroids);
            }

            centroidsFile = newCentroids;
            iteration ++;

        } while (!hasConverged && iteration < 5);
        //}while (!hasConverged);

        // now that we have computed the centroids
        writeFinalData(configuration, Utils.getCentroids(centroidsFile));
        
    }


    /**
     * executes the job
     *
     * @return true if the job has converged, else false
     */
    private static boolean launchJob(Configuration configuration) throws Exception {

        Job job = Job.getInstance(configuration);
        job.setJobName("KMeans");
        job.setJarByClass(KMeans.class);

        job.setMapperClass(KMeansMapper.class);
        job.setReducerClass(KMeansReducer.class);

        /*job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);*/

        job.setNumReduceTasks(1);
        
        HTableInterface table = SingletonConnection.getInstance("adjacency-matrix");
        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs
        
		TableMapReduceUtil.initTableMapperJob(
				  "adjacency-matrix",   		// input HBase table name
				  scan, 			// Scan instance to control CF and attribute selection
				  KMeansMapper.class,		// mapper
				  IntWritable.class,	// reducer value
				  Text.class,		// reducer key 
				  job			// job instance
				  );
        

        job.addCacheFile(new Path(Constants.CENTROIDS_FILE).toUri());
        
        // Erase previous run output (if any)
        // FileSystem.get(configuration).delete(new Path("counteroutput"), true);

        FileInputFormat.addInputPath(job, new Path(configuration.get(Constants.INPUT_FILE_ARG)));
        FileOutputFormat.setOutputPath(job, new Path(configuration.get(Constants.OUTPUT_FILE_ARG)));

        return job.waitForCompletion(true);
		
    }

    public static void writeFinalData(Configuration configuration, List<Double[]> centroids) throws IOException {

        FileSystem fs = FileSystem.get(configuration);
        // true stands for recursively deleting the folder you gave
        // fs.delete(new Path(Constants.OUTPUT_FILE_ARG), true);

        
        HBaseUtil util = new HBaseUtil();
        
        
        System.out.println("Writting KMeans centroids on given dataset to file: /user/dkari/centroids.dat");
        System.out.println("\nAlso KMeans centroids can be found in HBase table: kmeans-centroids");
        System.out.println("\n****** Final Centroids: ");
        int n = 0;
        for (Double[] doubles : centroids) {
        	util.persistCentroid(new Centroid(n+"" , (n++ +1), Arrays.toString(doubles)));
			/*for (int i = 0; i < doubles.length; i++) {
				System.out.println(doubles[i]);
				
			}*/
        	System.out.println(Arrays.toString(doubles));
		}
        
        
        // System.out.println("Writting final output to: " + configuration.get(Constants.OUTPUT_FILE_ARG) + "/final-data");
        
        FSDataOutputStream dataOutputStream = fs.create(new Path(configuration.get(Constants.OUTPUT_FILE_ARG) + "/final-data"));
        FSDataInputStream dataInputStream = new FSDataInputStream(fs.open(new Path(configuration.get(Constants.INPUT_FILE_ARG) + "/points.dat")));

        BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dataOutputStream));

        try {

            String line;
            while ((line = reader.readLine()) != null) {

                String[] values = line.split(" ");
                double x = Double.parseDouble(values[0]);
                double y = Double.parseDouble(values[1]);
                int index = 0;
                double minDistance = Double.MAX_VALUE;
                for (int j = 0; j < centroids.size(); j++) {
                    double distance = Utils.euclideanDistance(centroids.get(j)[0], centroids.get(j)[1], x, y);
                    if (distance < minDistance) {
                        index = j;
                        minDistance = distance;
                    }
                }

                writer.write(x + "\t" + y + "\t" + index + "\n");
            }
        }
        finally {

            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
    
	/*public final class OverwritingTextOutputFormat<K, V> extends
			TextOutputFormat<K, V> {
		public void checkOutputSpecs(JobContext job) throws IOException {
			// Nothing
		}
	}*/

}

package kmeans;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RandomRowFilter;

import hbase.HBaseUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<Double[]> readCentroids(String filename) throws IOException {

    	System.out.println("((((((((((((((((((((((((((((( Reading centroids from cache: ");
    	
        FileInputStream fileInputStream = new FileInputStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line;
        List<Double[]> centroids = new ArrayList<Double[]>();
        try {
            while ((line = reader.readLine()) != null) {
            	// throw new IOException("FILENAME: " + filename +" - LINE=[" + line + "]");
                String[] values = line.split("\t");
                String[] temp = values[0].split(" ");
                
                ArrayList<Double> centroid = new ArrayList<Double>();
                for (int i = 0; i < temp.length; i++) {
                	if (StringUtils.isNotEmpty(temp[i]))
                		centroid.add(Double.parseDouble(temp[i]));
				}
                
                /*Double[] centroid = new Double[2];
                centroid[0] = Double.parseDouble(temp[0]);
                centroid[1] = Double.parseDouble(temp[1]);
                centroids.add(centroid);*/
                Double[] centers = new Double[centroid.size()];
                for (int i=0; i< centroid.size(); i++) {
					centers[i] = centroid.get(i); 
				}
                centroids.add(centers);
                
                System.out.println(centroid.toString());
            }
        }
        finally {
            reader.close();
        }
        return centroids;

    }

    public static List<Double[]> getCentroids(String content) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line;
        List<Double[]> centroids = new ArrayList<Double[]>();
        try {
            while ((line = reader.readLine()) != null) {
//                throw new IOException("FILENAME: " + filename +" - LINE=[" + line + "]");
                String[] values = line.split("\t");
                String[] temp = values[0].split(" ");
                
                /*Double[] centroid = new Double[2];
                centroid[0] = Double.parseDouble(temp[0]);
                centroid[1] = Double.parseDouble(temp[1]);*/
                
                ArrayList<Double> centroid = new ArrayList<Double>();
                for (int i = 0; i < temp.length; i++) {
                	if (StringUtils.isNotEmpty(temp[i]))
                		centroid.add(Double.parseDouble(temp[i]));
				}
                
                Double[] centers = new Double[centroid.size()];
                for (int i=0; i< centroid.size(); i++) {
					centers[i] = centroid.get(i); 
				}
                centroids.add(centers);
            }
        }
        finally {
            reader.close();
        }

        return  centroids;
    }

    public static String getFormattedCentroids(List<Double[]> centroids) {

        int counter = 0;
        StringBuilder centroidsBuilder = new StringBuilder();
        for (Double[] centroid : centroids) {
        	for (Double double1 : centroid) {
        		centroidsBuilder.append(double1.toString());
                centroidsBuilder.append(" ");
			}
            /*centroidsBuilder.append(centroid[0].toString());
            centroidsBuilder.append(" ");
            centroidsBuilder.append(centroid[1].toString());*/
            centroidsBuilder.append("\t");
            centroidsBuilder.append("" + counter++);
            centroidsBuilder.append("\n");
        }
        System.out.println(centroidsBuilder.toString());
        return centroidsBuilder.toString();
    }

    public static void writeCentroids(Configuration configuration, String formattedCentroids) throws IOException {

        FileSystem fs = FileSystem.get(configuration);
        FSDataOutputStream fin = fs.create(new Path(Constants.CENTROIDS_FILE));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fin));
        bw.append(formattedCentroids);
        
        // in parllel write the generated centroids to HBase
        //HBaseUtil.
        
        bw.close();
    }

    public static List<Double[]> createRandomCentroids(int centroidsNumber) {

        List<Double[]> centroids = new ArrayList<Double[]>();

        // computes randomly centroids
        for (int j = 0; j < centroidsNumber; j++) {
            Double[] centroid = new Double[2];
            centroid[0] = Math.random() * 2;
            centroid[1] = Math.random() * 2;
            centroids.add(centroid);
        }

        return centroids;
    }


    public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    
    public static double euclideanDistance(Double[] point1, Double[] point2) {
    	double distance = 0.0d;
    	for (int i=0; i < point1.length; i++){
    		// for (int j=0; j < point2.length; j++){
    			//if (i ==j){
    				distance += Math.sqrt(Math.pow(point1[i] - point2[i], 2));
    			//}
    		//}
    	}
        return distance; //Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static String readReducerOutput(Configuration configuration) throws IOException {
        FileSystem fs = FileSystem.get(configuration);
        FSDataInputStream dataInputStream = new FSDataInputStream(fs.open(new Path(configuration.get(Constants.OUTPUT_FILE_ARG) + "/part-r-00000")));
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        return content.toString();
    }

    public static ArrayList<Double[]> getKRandomRows(int k) throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTable table = new HTable(conf, "adjacency-matrix");

		ArrayList<Double[]> kDataPoints = new ArrayList<Double[]>();

		Filter filter = new RandomRowFilter((float) Math.random());
		Scan scan = new Scan();
		scan.setFilter(filter);
		ResultScanner scanner = table.getScanner(scan);
		int n = 1;
		for (Result result : scanner) {
			System.out.println(n + ": " + result);
			kDataPoints.add(new HBaseUtil().readDataPoint(result));
			if (n++ == k) {
				break;
			}
		}
		scanner.close();
		table.close();

		return kDataPoints;
	}
    
    public static void main(String[] args) throws IOException {
    	List<Double[]> lists = new ArrayList<Double[]>();
    	Double[] arr1 = new Double[]{0.01d, 0.02d, 0.03d, 0.04d};
    	Double[] arr2 = new Double[]{0.11d, 0.22d, 0.33d, 0.44d};
    	
    	lists.add(arr1);
    	lists.add(arr2);
    	
    	// 0.01 0.02 0.03 0.04 	0
    	// 0.11 0.22 0.33 0.44 	1
    	getFormattedCentroids(lists);
    	
    	// 0.62 808.5 367.5 220.5 3.5 4.0 0.0 0.0 	0
    	// 0.71 710.5 269.5 220.5 3.5 5.0 0.1 1.0 	1
    	// 0.62 808.5 367.5 220.5 3.5 5.0 0.0 0.0 	2
    	ArrayList<Double[]> rows = getKRandomRows(3);
    	getFormattedCentroids(rows);
    	
		List<Double[]> centriods = getCentroids("0.79 637.0 343.0 147.0 7.0 5.0 0.1 1.0 1"
				+ "" + "0.82 612.5 318.5 147.0 7.0 2.0 0.1 1.0	0");
		getFormattedCentroids(centriods);
	}
}

package kmeans;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import com.slb.analytics.hbase.HBaseUtil;

public class KMeansMapper extends TableMapper<IntWritable, Text> {

    public static List<Double[]> centroids;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] cacheFiles = context.getCacheFiles();
        centroids = Utils.readCentroids(cacheFiles[0].toString());
    
    }

    @Override
    public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {

        /*String[] xy = value.toString().split(" ");
        double x = Double.parseDouble(xy[0]);
        double y = Double.parseDouble(xy[1]);*/
    	
    	// Rowkey
    	// row.get();
    	
    	Double[] dataPoint = new HBaseUtil().readDataPoint(value);
    	System.out.println("***************** Mapper Input: " + value.toString());
    	System.out.println("***************** DataPoint: " + dataPoint.toString());
    	
    	
    	for (Double[] centroid : centroids) {
    		StringBuilder s = new StringBuilder();
    		for (int i=0; i<centroid.length; i++){
    		//for (Double double1 : centroid) {
        		s = s.append(centroid[i].toString());
                
        		if (i != (centroid.length-1))
        		s.append(" ");
			}
    		System.out.println("***************** Centroids: " + s);
    	}
    	
        
        int index = 0;
        double minDistance = Double.MAX_VALUE;
        for (int j = 0; j < centroids.size(); j++) {
            double distance = Utils.euclideanDistance(centroids.get(j), dataPoint);
            if (distance < minDistance) {
                index = j;
                minDistance = distance;
            }
        }
    	Text txt = new Text();
    	
    	StringBuilder centroidsBuilder = new StringBuilder();
        for (Double double1 : dataPoint) {
    		centroidsBuilder.append(double1.toString());
            centroidsBuilder.append(" ");
		}
        // centroidsBuilder.append("\t");
        // centroidsBuilder.append("\n");
        txt.set(centroidsBuilder.toString());
        
        System.out.println("############ Mapper output: " + centroidsBuilder.toString() + ".       Key: " + index);
    	
        context.write(new IntWritable(index), txt);
    }
}

package kmeans;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class KMeansReducer extends
		Reducer<IntWritable, Text, Text, IntWritable> {

	protected void reduce(IntWritable key, Iterable<Text> values,
			Context context) throws IOException, InterruptedException {

		List<Double> centroids = new ArrayList<Double>();

		int itr = 0;
		int rowSize = 0;
		int colSize = 0;

		ArrayList<Text> vals = new ArrayList<Text>();
		System.out.println("Start reducing ................");
		for (Text value : values) {
			System.out.println("Values: >>> " + value.toString());
			vals.add(value);
		}

		for (Text value : vals) {
			rowSize++;

			if (itr == 0) {
				String[] data = (value.toString()).split(" ");
				colSize = data.length;
			}
			itr++;
		}

		Double[][] centers = new Double[rowSize][colSize];

		int n = 0;
		for (Text value : vals) {
			System.out.println("value" + value.toString());

			String[] temp = (value.toString().trim()).split(" ");

			if (temp.length == colSize) {
				for (int i = 0; i < colSize; i++) {
					// System.out.println(" x=" + n + "  y=" + i + " " +
					// temp[i]);
					if (StringUtils.isNotBlank(temp[i].trim())) {
						centers[n][i] = Double.parseDouble(temp[i].trim());
					} else {
						centers[n][i] = 0.0;
					}
				}
				n++;
			} else {
				rowSize = rowSize - 1;
			}
		}

		for (int i = 0; i < colSize; i++) {
			Double colSum = 0.0;
			for (int j = 0; j < centers.length; j++) {
				if (null != centers[j][i])
					colSum += centers[j][i];
			}
			centroids.add(colSum);
		}

		System.out.println("New Centroid Size: " + centroids.size() + "   "
				+ centroids.toString());

		StringBuilder dataPoint = new StringBuilder();
		DecimalFormat df = new DecimalFormat("#.##");
		for (Double point : centroids) {
			double mean = Double.valueOf(df.format(point / n));
			dataPoint.append(mean);
			dataPoint.append(" ");
		}

		String centroid = dataPoint.toString().trim();

		System.out.println("New Mean Centroid:  >>>  " + centroid);

		/*
		 * StringBuffer sb = new StringBuffer();
		 * 
		 * for (Text value: values) { sb.append(value.toString()); }
		 */

		context.write(new Text(centroid), key);
	}

}

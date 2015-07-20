package kmeans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.io.Text;

public class TestDistanceCalc {
	public static void main(String[] args) {
		new TestDistanceCalc().test();
	}

	private void test() {
		
		System.out.println("Finding the new centroid");
		
		ArrayList<Text> values = new ArrayList<Text>() {{
			add(new Text("0.82 612.5 318.5 147.0 7.0 5.0 0.1 1.0 "));
			add(new Text("0.86 588.0 294.0 147.0 7.0 2.0 0.1 1.0 "));
			add(new Text("0.9 563.5 318.5 122.5 7.0 2.0 0.1 1.0 "));
			add(new Text("0.76 661.5 416.5 122.5 7.0 5.0 0.1 1.0 "));
			add(new Text("0.74 686.0 245.0 220.5 3.5 4.0 0.1 1.0 "));
			add(new Text("0.74 686.0 245.0 220.5 3.5 2.0 0.1 1.0 "));
			add(new Text("0.82 612.5 318.5 147.0 7.0 2.0 0.1 1.0 "));
			add(new Text("0.76 661.5 416.5 122.5 7.0 2.0 0.1 1.0 "));
			add(new Text("0.79 637.0 343.0 147.0 7.0 3.0 0.1 1.0 "));
			add(new Text("0.74 686.0 245.0 220.5 3.5 3.0 0.1 1.0 "));
			add(new Text("0.76 661.5 416.5 122.5 7.0 3.0 0.1 1.0 "));
			add(new Text("0.86 588.0 294.0 147.0 7.0 4.0 0.1 1.0 "));
			add(new Text("0.9 563.5 318.5 122.5 7.0 3.0 0.1 1.0 "));
			add(new Text("0.79 637.0 343.0 147.0 7.0 4.0 0.1 1.0 "));
			add(new Text("0.82 612.5 318.5 147.0 7.0 3.0 0.1 1.0 "));
			add(new Text("0.9 563.5 318.5 122.5 7.0 5.0 0.1 1.0 "));
			add(new Text("0.86 588.0 294.0 147.0 7.0 5.0 0.1 1.0 "));
			add(new Text("0.79 637.0 343.0 147.0 7.0 5.0 0.1 1.0 "));
			add(new Text("0.74 686.0 245.0 220.5 3.5 5.0 0.1 1.0 "));
			add(new Text("0.82 612.5 318.5 147.0 7.0 4.0 0.1 1.0 "));
			add(new Text("0.76 661.5 416.5 122.5 7.0 4.0 0.1 1.0 "));
			add(new Text("0.86 588.0 294.0 147.0 7.0 3.0 0.1 1.0 "));
			add(new Text("0.79 637.0 343.0 147.0 7.0 2.0 0.1 1.0 "));
			add(new Text("0.9 563.5 318.5 122.5 7.0 4.0 0.1 1.0 "));
		}};
		
		List<Double> centroids = new ArrayList<Double>();
        
	       int itr = 0;
	       int rowSize = 0;
	       int colSize = 0;
	       
	       System.out.println("Start reducing ................");
	       for (Text value : values) {
	    	   System.out.println("Values: >>> " + value.toString());
	       }
	       
			for (Text value : values) {
				rowSize++;
				
				if (itr == 0){
					String[] data = (value.toString()).split(" ");
					colSize = data.length;
				}
				itr++;
			}

			Double[][] centers = new Double[rowSize][colSize];

			int n = 0;
			for (Text value : values) {
				System.out.println("value"
						+ value.toString());

				String[] temp = (value.toString().trim()).split(" ");
				
				if (temp.length == colSize){
					for (int i = 0; i < colSize; i++) {
						//System.out.println(" x=" + n + "  y=" + i + " " + temp[i]);
						if (StringUtils.isNotBlank(temp[i].trim())){
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

			System.out.println("New Centroid Size: "
					+ centroids.size() + "   " + centroids.toString());

			StringBuilder dataPoint = new StringBuilder();
			for (Double point : centroids) {
				double mean = point/n;
				dataPoint.append(mean);
				dataPoint.append(" ");
			}

			String centroid = dataPoint.toString().trim();

			System.out.println("New Mean Centroid:  >>>  " + centroid);
	}
}

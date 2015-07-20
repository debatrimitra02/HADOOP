package hbase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ExportCsv {
	public void readAndPersistData(String inFilePath){
	
				
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
	 
		try {
	 
			br = new BufferedReader(new FileReader(inFilePath));
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] cols = line.split(cvsSplitBy);
	 
				/*System.out.println("DataPoint [X1= " + cols[1] 
	                                 + " , X2=" + cols[2] + "]");*/
				DataPoint point = null;
				if (null != cols && cols.length > 0 && !cols[1].equalsIgnoreCase("X1")){
					point = new DataPoint();
					point.setUserID(cols[0]);
					point.setX1(Double.parseDouble(cols[1]));
					point.setX2(Double.parseDouble(cols[2]));
					point.setX3(Double.parseDouble(cols[3]));
					point.setX4(Double.parseDouble(cols[4]));
					point.setX5(Double.parseDouble(cols[5]));
					point.setX6(Double.parseDouble(cols[6]));
					point.setX7(Double.parseDouble(cols[7]));
					point.setX8(Double.parseDouble(cols[8]));
				
	 
					// Open Hbase connection and write it to HBase table
					new HBaseUtil().persistDataPoint(point);
				}
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
	  }

	public static void main(String[] args) throws IOException {
		ExportCsv obj = new ExportCsv();
		obj.readAndPersistData(args[0]);
		
		// reading from HBase
		// new HBaseUtil().scanDataPoints();
	}
}

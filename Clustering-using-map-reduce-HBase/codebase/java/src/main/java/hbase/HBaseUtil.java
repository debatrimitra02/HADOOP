package hbase;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Map.Entry;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseUtil {
	public void persistDataPoint(DataPoint point) {
		try {
			//String rowKey = System.currentTimeMillis() + "1";
			String rowKey = point.getUserID();

			// Configuration conf = HBaseConfiguration.create();
			HTableInterface table = SingletonConnection.getInstance("adjacency-matrix");
			
			List<Put> puts = new ArrayList<Put>();
			Put put1 = new Put(Bytes.toBytes(rowKey));
			put1.add(Bytes.toBytes("cf"), Bytes.toBytes("X1"),
					Bytes.toBytes(point.getX1()));
			puts.add(put1);

			Put put2 = new Put(Bytes.toBytes(rowKey));
			put2.add(Bytes.toBytes("cf"), Bytes.toBytes("X2"),
					Bytes.toBytes(point.getX2()));
			puts.add(put2);

			Put put3 = new Put(Bytes.toBytes(rowKey));
			put3.add(Bytes.toBytes("cf"), Bytes.toBytes("X3"),
					Bytes.toBytes(point.getX3()));
			puts.add(put3);

			Put put4 = new Put(Bytes.toBytes(rowKey));
			put4.add(Bytes.toBytes("cf"), Bytes.toBytes("X4"),
					Bytes.toBytes(point.getX4()));
			puts.add(put4);

			Put put5 = new Put(Bytes.toBytes(rowKey));
			put5.add(Bytes.toBytes("cf"), Bytes.toBytes("X5"),
					Bytes.toBytes(point.getX5()));
			puts.add(put5);

			Put put6 = new Put(Bytes.toBytes(rowKey));
			put6.add(Bytes.toBytes("cf"), Bytes.toBytes("X6"),
					Bytes.toBytes(point.getX6()));
			puts.add(put6);

			Put put7 = new Put(Bytes.toBytes(rowKey));
			put7.add(Bytes.toBytes("cf"), Bytes.toBytes("X7"),
					Bytes.toBytes(point.getX7()));
			puts.add(put7);

			Put put8 = new Put(Bytes.toBytes(rowKey));
			put8.add(Bytes.toBytes("cf"), Bytes.toBytes("X8"),
					Bytes.toBytes(point.getX8()));
			puts.add(put8);

			table.put(puts);
			table.flushCommits();
			table.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<DataPoint> scanDataPoints() throws IOException {
		// Configuration conf = HBaseConfiguration.create();
		HTableInterface table = SingletonConnection.getInstance("adjacency-matrix");
		 
		try {
			Scan s = new Scan();
			ResultScanner ss = table.getScanner(s);
			for (Result r : ss) {
				for (KeyValue kv : r.raw()) {
					System.out.print("getAllUsers ::::::: " + new String(kv.getRow()) + " ");
					System.out.print(new String(kv.getFamily()) + ":");
					System.out.print(new String(kv.getQualifier()) + " ");
					System.out.print(kv.getTimestamp() + " ");
					byte[] val = kv.getValue();
					System.out.println(ByteBuffer.wrap(val).getDouble());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public Map<String, Double[]> getAllDataPoints() throws IOException {
		HTableInterface table = SingletonConnection.getInstance("adjacency-matrix");
		 
		try {
			Scan s = new Scan();
			ResultScanner ss = table.getScanner(s);
			for (Result r : ss) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Double[] readDataPoint(Result row) {

		ArrayList<Double> dataPoint = new ArrayList<Double>();

		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> allVersions = row
				.getMap();

		for (Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyEntry : allVersions
				.entrySet()) {
			NavigableMap<byte[], NavigableMap<Long, byte[]>> columnMap = columnFamilyEntry
					.getValue();
			for (Entry<byte[], NavigableMap<Long, byte[]>> columnEntry : columnMap
					.entrySet()) {
				NavigableMap<Long, byte[]> cellMap = columnEntry.getValue();

				for (Entry<Long, byte[]> cellEntry : cellMap.entrySet()) {

					/*System.out.println(String.format(
							"TimeStamp: %s, Column : %s, Value :%s",
							cellEntry.getKey(),
							Bytes.toString(columnEntry.getKey()),
							Bytes.toDouble(cellEntry.getValue())));*/

					// String column = Bytes.toString(columnEntry.getKey());
					// Double value = Bytes.toDouble(cellEntry.getValue());
					dataPoint.add(Bytes.toDouble(cellEntry.getValue()));
				}
				// break;
			}
		}

		return dataPoint.toArray(new Double[dataPoint.size()]);
	}
	
	public Map<String, Double[]> readDataPoint1(Result row) {

		String rowKey = null;
		ArrayList<Double> dataPoint = new ArrayList<Double>();

		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> allVersions = row
				.getMap();

		for (Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> columnFamilyEntry : allVersions
				.entrySet()) {
			NavigableMap<byte[], NavigableMap<Long, byte[]>> columnMap = columnFamilyEntry
					.getValue();
			for (Entry<byte[], NavigableMap<Long, byte[]>> columnEntry : columnMap
					.entrySet()) {
				NavigableMap<Long, byte[]> cellMap = columnEntry.getValue();

				for (Entry<Long, byte[]> cellEntry : cellMap.entrySet()) {

					/*System.out.println(String.format(
							"TimeStamp: %s, Column : %s, Value :%s",
							cellEntry.getKey(),
							Bytes.toString(columnEntry.getKey()),
							Bytes.toDouble(cellEntry.getValue())));*/

					// String column = Bytes.toString(columnEntry.getKey());
					// Double value = Bytes.toDouble(cellEntry.getValue());
					dataPoint.add(Bytes.toDouble(cellEntry.getValue()));
				}
				// break;
			}
		}
		
		// return dataPoint.toArray(new Double[dataPoint.size()]);
		return null;
	}
	
	public void persistCentroid(Centroid center){
		try {
			String rowKey = center.getKey();

			HTableInterface table = SingletonConnection.getInstance("kmeans-centroids");
			
			List<Put> puts = new ArrayList<Put>();
			Put put1 = new Put(Bytes.toBytes(rowKey));
			put1.add(Bytes.toBytes("cf"), Bytes.toBytes("sequence"),
					Bytes.toBytes(center.getSequence()));
			puts.add(put1);

			Put put2 = new Put(Bytes.toBytes(rowKey));
			put2.add(Bytes.toBytes("cf"), Bytes.toBytes("centroid"),
					Bytes.toBytes(center.getCentroid()));
			puts.add(put2);

			table.put(puts);
			table.flushCommits();
			table.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public List<Centroid> scanCentroids() throws IOException {
		HTableInterface table = SingletonConnection.getInstance("kmeans-centroids");
		 
		try {
			Scan s = new Scan();
			ResultScanner ss = table.getScanner(s);
			for (Result r : ss) {
				for (KeyValue kv : r.raw()) {
					System.out.print("Get All Centroids ::::::: " + new String(kv.getRow()) + " ");
					System.out.print(new String(kv.getFamily()) + ":");
					System.out.print(new String(kv.getQualifier()) + " ");
					System.out.print(kv.getTimestamp() + " ");
					byte[] val = kv.getValue();
					System.out.println("Centroid" + Bytes.toString(val));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		new HBaseUtil().scanCentroids();
	}
}

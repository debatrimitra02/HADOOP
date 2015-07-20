package hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

public class SingletonConnection {

	final static Logger logger = Logger.getLogger(SingletonConnection.class);

	private static HConnection connection = null;
	private static Object obj = new Object();

	private SingletonConnection() {
		synchronized (obj) {
			if (connection == null) {
				Configuration config = HBaseConfiguration.create();				
				try {

					// HBaseAdmin.checkHBaseAvailable(config);
					connection = HConnectionManager.createConnection(config);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static HTableInterface getInstance(String tablename)
			throws IOException {
		if (connection == null) {
			synchronized (obj) {
				new SingletonConnection();
			}
		}
		HTableInterface table = connection.getTable(tablename);
		table.setAutoFlushTo(true);
		return table;
	}

	public static void main(String[] args) throws Exception {
		HTableInterface table = SingletonConnection
				.getInstance("digital-hr-users");

		System.out.println("Got the connection: "
				+ table);
		Scan s = new Scan();
		s.addColumn(Bytes.toBytes("u"), Bytes.toBytes("username"));
		ResultScanner scanner = table.getScanner(s);
		try {

			System.out.println("Running the scan....");
			for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
				System.out.println("Found row: " + rr);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}

package NET.webserviceX.www;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.TimerTask;

// Create a class extends with TimerTask
public class ScheduledTask extends TimerTask {
	Map<String, ArrayList<String>> symbolMap = new HashMap<String, ArrayList<String>>();
	//ArrayList<String> priceList = new ArrayList<>();
	// Add your task here
	
	public void run() {
		readFile("symbols.txt");
	}

	public ScheduledTask() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*public ScheduledTask(Map<String, ArrayList<String>> symbolMap) {
		super();
		this.symbolMap = symbolMap;
		// TODO Auto-generated constructor stub
	}*/
	
	private void writeToFile(String symbol) {
		try {
			StockQuoteSoapProxy quoteSoapProxy = new StockQuoteSoapProxy();

			String data = quoteSoapProxy.getQuote(symbol);
			int startIndex = data.indexOf("<Last>") + 6;
			int endIndex = data.indexOf("</Last>");
			String price = data.substring(startIndex, endIndex);
			File file1 = new File("result.txt");

			// if file doesnt exists, then create it
			if (!file1.exists()) {
				file1.createNewFile();
			}

			// true = append file
			FileWriter fileWritter = new FileWriter(file1.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write("<" + symbol + ",");
			bufferWritter.write(price + ">\n");
			bufferWritter.close();

			/*ArrayList<String> priceList = new ArrayList<>();
			if(symbolMap.containsKey(symbol)){
				priceList = symbolMap.remove(symbol);
				
			} else {
				//priceList.add(price);
				//symbolMap.put(symbol, priceList);
			}
			priceList.add(price);
			symbolMap.put(symbol, priceList);*/
			System.out.println("Done");
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readFile(String fileName) {
		try {
			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				writeToFile(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

	

}
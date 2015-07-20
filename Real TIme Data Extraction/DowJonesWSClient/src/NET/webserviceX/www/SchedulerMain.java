package NET.webserviceX.www;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;

//Main class
public class SchedulerMain {
	public static void main(String args[]) throws InterruptedException {
		Map<String, ArrayList<String>> symbolMap = new HashMap<String, ArrayList<String>>();
		Timer time = new Timer(); // Instantiate Timer Object
		ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
		time.schedule(st, 0, 10000); // Create Repetitively task for every 10 mins

		// for demo only.
		for (int i = 0; i <= 2; i++) {
			System.out.println("Execution in Main Thread...." + i);
			Thread.sleep(10000);
			if (i == 2) {
				System.out.println("Application Terminates");
				
				 Map<String, ArrayList<String>> resultMap = prepareMap();
				 
				 
				printMap(resultMap);
				System.exit(0);
			}
		}

		/*ArrayList<String> Arr = new ArrayList<String>();
		Arr.add("1");
		Arr.add("2");
		Arr.add("3");
		System.out.println("STD:"+std(Arr));*/
		

	}
	
	public static Map<String, ArrayList<String>> prepareMap() {
		Map<String, ArrayList<String>> symbolMap = new HashMap<String, ArrayList<String>>();
		
		try {
			File file = new File("result.txt");
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				
				String currentLine = scanner.nextLine();//<AAPL,128.62>
				String temString = currentLine.substring(1, currentLine.length()-1);
				String[] strArray = temString.split(",");
				String key = strArray[0];
				String value = strArray[1];
				
				
				ArrayList<String> priceList = new ArrayList<>();
				if(symbolMap.containsKey(key)){
					priceList = symbolMap.remove(key);
					
				}
				priceList.add(value);
				symbolMap.put(key, priceList);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return symbolMap;
		
	}
	
	public static double std(ArrayList<String> Arr) {
		
		float Sum=0.0f;
		
		for (String arrItem : Arr) {		
			Sum=Sum+Float.parseFloat(arrItem);
		}
		System.out.println("Sum:"+Sum);
		double mean = (Sum/Arr.size());
		
		double stdsum = 0;
		
		for (String arrItem : Arr) {		
			stdsum = stdsum + Math.pow(Double.parseDouble(arrItem)-mean, 2);
		}
		
		return (stdsum/Arr.size());
		
	}
	
	
	public static void printMap(Map mp) {
	    Iterator it = mp.entrySet().iterator();
	    
	    String symbol="";
	    double highest =0;
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        
	        double std = std((ArrayList<String>) pair.getValue());
	        
	        if(highest< std){
	        	highest = std;
	        	symbol = (String) pair.getKey();
	        }
	        
	     /*   ArrayList arList =  (ArrayList) pair.getValue();
	        
	        for (Object object : arList) {
				System.out.println(object.toString());
			}*/
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    System.out.println("Symbol:" + symbol +": High:" + highest );
	}
}
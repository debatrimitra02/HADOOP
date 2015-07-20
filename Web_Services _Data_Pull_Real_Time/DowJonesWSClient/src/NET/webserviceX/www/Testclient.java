package NET.webserviceX.www;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Testclient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*StockQuoteSoapProxy quoteSoapProxy = new StockQuoteSoapProxy();
		System.out.println("SEI:" + quoteSoapProxy.getEndpoint());
		//http://localhost:10993/stockquote.asmx
		try {
			System.out.println("Result:" + quoteSoapProxy.getQuote("IBM"));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		File file=new File("C:\\12043027.txt");
	    String text="myText";
	    FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(text.getBytes());
		    fileOutputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

}

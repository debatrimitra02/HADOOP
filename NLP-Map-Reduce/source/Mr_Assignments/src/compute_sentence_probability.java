import java.io.*;
import java.util.*;

class compute_sentence_probability 
{

	public void compute_prob(String filename)
	{
		String line = null;
		try 
		{
			FileReader fileReader = new FileReader(filename);
			 BufferedReader bufferedReader = new BufferedReader(fileReader);
			 
			 try {
				while((line = bufferedReader.readLine()) != null) 
				 	{
				        System.out.println(line);
				    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void main()
	{
		compute_prob("Corpus.txt");
	}
	
	
}



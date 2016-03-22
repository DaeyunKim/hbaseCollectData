package collectData;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class writePaper {
	BufferedWriter writer=null;
	
	public writePaper(){
		
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("G://test.txt"));	
			
			
		}catch(Exception e){
			
		}
		
		
	}
	public void openWriter(String paper){
		try{
			writer.write("\n"+paper+"\r");	
		}catch(Exception ex){
			
		}
		
		
	}

	
	public void closeWriter(){
		try{
			writer.close();
		}catch(Exception ex){
			
		}
	}
	
}


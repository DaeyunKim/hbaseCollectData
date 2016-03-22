package collectData;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class MainCollect {

	public static void main(String []args) throws IOException, ParserConfigurationException, SAXException{
			
	//	new source();
		String str = "rlaeodbs";
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
			System.out.println(MD5);
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
			
	}
}

package hbase_Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class writePaper {

	public static void main(String[]args){
		
		String str = "그리드 데이터베이스에서 링 기반 연결 구조를 이용한 부하 분산 기법+database";
		String MD5 = "그리드 데이터베이스에서 링 기반 연결 구조를 이용한 부하 분산 기법+database"; 
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
		System.out.println(MD5);
		
	}
}


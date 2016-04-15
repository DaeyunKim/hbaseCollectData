package hbase_Test;

import java.util.ArrayList;

public class paperInfo {

	String category=""; //카테고리
	String title=""; //제목
	public ArrayList<Author> author = new ArrayList<Author>();//저자정보
	
	String publisher_url="null"; // 학회 정보 url
	String publisher_name="null"; // 학회 이름
	String Issue_date="null"; //논문 발표 날짜
	String linkURL="null"; //논문 URL
	String Issue_name="null"; 
	String Issue_number="0";
	ArrayList<String> each_author = new ArrayList<String>();
public	ArrayList<String> paper_keyword = new ArrayList<String>();

	public paperInfo(String category){
		this.category=category;
	}
	String callAuthor(){
		String str = "";
		
		for(Author o : author){
			
			//str=o.order+";"+o.name+";"+o.url;
			str+=o.name+";";
			//1
		}			
		
		return str; 
	}
	
	ArrayList<String> eachAuthor(){
		
		for(Author o : author){
			
			each_author.add(o.name);
			
		}	
				
		return each_author; 
	}
	
	String callAuthorURL(){
		String str = null;
		
		for(Author o : author){
			
			//str=o.order+";"+o.name+";"+o.url;
			if(o.url==null){
				o.url="null";
				str=o.url+";";	
				
			}
			str+=o.url;
			
			//1
		}			
		System.out.println("authorURL"+str);
		return str; 
	}
	
	
	String transAuthorSize(){
		return String.valueOf(author.size());		
		
	}
	
	public void  setKeyWords(ArrayList<String> keywords){
		for(String s:keywords){
			paper_keyword.add(s);	
		}		
	};
	
	
	
	//connectDB(){}
	
	public String toString( ){
		
		String paper_Info = null;
		
		paper_Info = category+";"+title +";"+publisher_url+";"+publisher_name+";"+Issue_date+";"+linkURL+";"+Issue_name+";"
		+Issue_number+";"+author.size()+";"+callAuthor();
		
		return paper_Info;
	}
	
	
	
	
}

package connect.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ConnectRDB {
	
	static final int AUTHOR = 0;
	static final int KEYWORD = 1;

	Connection con = null;
	Statement st = null;
	ResultSet rs = null;
	
	String url = "jdbc:postgresql://203.255.77.147/expertdb";
	String user = "expert";
	String password = "netdb3230";
	public ArrayList<String> paper_keyword;
	
	public ConnectRDB() {
		// TODO Auto-generated constructor stub
		getConnection();
	}
	
	private void getConnection(){
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			con = DriverManager.getConnection(url, user, password);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	

	public ArrayList<Integer> getNum(ArrayList<String> querys, int flag){
		try {
			st = con.createStatement();
			ArrayList<Integer> rtv = new ArrayList<Integer>();
			String table = "";
			if(flag == ConnectRDB.AUTHOR)
				table  = "t_name_to_num";
			else if (flag == ConnectRDB.KEYWORD)
				table  = "t_key_to_num";
			else
				return null;
			
			int temp = 0;
			for( String query : querys){
				
				temp = getXNum(query, table);
				if(temp  == -1)
				{					
					do{
						insXNum(query,table);
						temp = getXNum(query,table);
					}while(temp == -1);
				}
				rtv.add(temp);
			}
			
			return rtv;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	
	private int getXNum(String s, String table)
	{
		try {
			String squery = "select num from " + table + " where name = ?";
			PreparedStatement spstmt = null;		
			spstmt = con.prepareStatement(squery);
			
			spstmt.setString(1, s);
			rs = spstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt("num");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e. printStackTrace();
		}
		return -1;
	}
	
	private void insXNum(String s, String table){
		try {
			String iquery = "insert into " + table + " values(?)";
			PreparedStatement ipstmt = null;
			ipstmt = con.prepareStatement(iquery);
			ipstmt.setString(1, s);
			ipstmt.executeUpdate();				
		} catch (Exception e) {
			// TODO: handle exception
			e. printStackTrace();
		}
	}	
	
	public void close(){
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ConnectRDB ps = new ConnectRDB();
		ArrayList<String> authors = new ArrayList<>();
		authors.add("최도진");
		authors.add("김대윤");		
		ArrayList<Integer> a = ps.getNum(authors,ConnectRDB.AUTHOR);
		
		System.out.println(a.toString());
		
		ArrayList<String> keywords = new ArrayList<>();
		keywords.add("Database");
		keywords.add("SNS");
		ArrayList<Integer> b = ps.getNum(keywords, ConnectRDB.KEYWORD);
		System.out.println("=============");
		
		System.out.println(b.toString());
		
		ps.close();
		
		
	}

}
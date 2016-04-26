import java.io.IOException;
import java.util.ArrayList;

import connect.hbase.ConnectHBase;
import connect.rdb.ConnectRDB;
import hbase_Test.paperInfo;

public class callpaperinfomain {

	public static void main(String args[]) throws IOException{
		ConnectRDB crdb = new ConnectRDB();
		ConnectHBase chbase = new ConnectHBase();
		String name = "유재수";
		String table = "t_name_to_num";
		
		
		ArrayList<paperInfo>loadData =  chbase.getMake(crdb.getXNum(name, table));//
		
		
		
		
	}
}

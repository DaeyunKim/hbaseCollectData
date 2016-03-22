package hbaseconnect;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class createHTable extends IOException{
	Configuration conf;
	HBaseAdmin admin;
	HTable table, table1, table2, table3, table4, table5;

	public createHTable() throws IOException {
		mkconfig();
		mkAdmin();
		createTable();
	}

	private void mkconfig() {

		conf = HBaseConfiguration.create();
		conf.clear();
		conf.set("hbase.master", "127.0.0.1");
		conf.set("hbase.zookeeper.quorum", "127.0.0.1");

	}

	private void mkAdmin() {

		try {
			admin = new HBaseAdmin(conf);
		} catch (MasterNotRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZooKeeperConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTable() throws IOException {
		if (!admin.isTableAvailable("T_TABLE_INFO")) {

			HTableDescriptor H_T_TABLE = new HTableDescriptor("T_TABLE_INFO");
			H_T_TABLE.addFamily(new HColumnDescriptor("paper_info"));
			H_T_TABLE.addFamily(new HColumnDescriptor("issue_info"));
			H_T_TABLE.addFamily(new HColumnDescriptor("url"));
			H_T_TABLE.addFamily(new HColumnDescriptor("keyword"));
			admin.createTable(H_T_TABLE);
			table = new HTable(conf, "T_TABLE_INFO");

		}

		if (!admin.isTableAvailable("T_EXPERT_INFO")) {
			HTableDescriptor H_T_EXPERT_INFO = new HTableDescriptor("T_EXPERT_INFO");
			H_T_EXPERT_INFO.addFamily(new HColumnDescriptor("paper_info"));
			admin.createTable(H_T_EXPERT_INFO);
			table1 = new HTable(conf, "T_EXPERT_INFO");
			System.out.println("Auto flush: " + table1.isAutoFlush());
		}

		if (!admin.isTableAvailable("T_KEYWORD_INFO")) {
			HTableDescriptor H_T_KEYWORD_INFO = new HTableDescriptor("T_KEYWORD_INFO");
			H_T_KEYWORD_INFO.addFamily(new HColumnDescriptor("paper_info"));
			admin.createTable(H_T_KEYWORD_INFO);
			table2 = new HTable(conf, "T_KEYWORD_INFO");
			System.out.println("Auto flush: " + table2.isAutoFlush());
		}

		if (!admin.isTableAvailable("T_KCIIF_INFO")) {
			HTableDescriptor H_T_KCIIF_INFO = new HTableDescriptor("T_KCIIF_INFO");
			H_T_KCIIF_INFO.addFamily(new HColumnDescriptor("Impact_factor"));
			admin.createTable(H_T_KCIIF_INFO);
			table4 = new HTable(conf, "T_KCIIF_INFO");
		}

		if (!admin.isTableAvailable("MT_P_SCORE")) {
			HTableDescriptor H_MT_P_SCORE = new HTableDescriptor("MT_P_SCORE");
			H_MT_P_SCORE.addFamily(new HColumnDescriptor("pscore"));
			admin.createTable(H_MT_P_SCORE);
			table5 = new HTable(conf, "MT_P_SCORE");
		}

		if (!admin.isTableAvailable("T_PAPER_CITATION_INFO")) {
			HTableDescriptor H_T_PAPER_CITATION_INFO = new HTableDescriptor("T_PAPER_CITATION_INFO");
			H_T_PAPER_CITATION_INFO.addFamily(new HColumnDescriptor("cite_info"));
			admin.createTable(H_T_PAPER_CITATION_INFO);
			table3 = new HTable(conf, "T_PAPER_CITATION_INFO");
			System.out.println("Auto flush: " + table2.isAutoFlush());
		}

	}

	public void insertPaperInfo(String url, String title, int nAuthor, String Author_names, String issue_number,
			String issue_date, String issue_name, String publisher_name, String authorURL, String paperURL,
			String publisherURL, String keywords) throws IOException {
		
		Put put = new Put(Bytes.toBytes(changeMD5(url)));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("title"), Bytes.toBytes(title));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("nAuthor"), Bytes.toBytes(nAuthor));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("Author_names"), Bytes.toBytes(Author_names));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_number"), Bytes.toBytes(issue_number ));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_date"), Bytes.toBytes(issue_date));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_name"), Bytes.toBytes(issue_name));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("publisher_name"), Bytes.toBytes(publisher_name));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("authorURL"), Bytes.toBytes(authorURL));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("paperURL"), Bytes.toBytes(paperURL));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("publisherURL"), Bytes.toBytes(publisherURL));
		put.add(Bytes.toBytes("keyword"), Bytes.toBytes("keywords"), Bytes.toBytes(keywords));

		table.put(put);
		table.flushCommits();

	}

	public void insertExpertInfo(String nameKeyword, String paper_id, String Author_classify) throws IOException {
		Put put = new Put(Bytes.toBytes(changeMD5(nameKeyword)));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("Paper_id"), Bytes.toBytes(paper_id));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("Author_classify"), Bytes.toBytes(Author_classify));
		table1.put(put);
		table1.flushCommits();
	}

	public void insertKeywordInfo(String keyword, String paper_id) throws IOException {
		Put put = new Put(Bytes.toBytes(changeMD5(keyword)));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("Paper_id"), Bytes.toBytes(paper_id));
		table2.put(put);
		table2.flushCommits();
	}

	public void insertPaperCitationInfo(String paper_id, String nCitation, String Citation_year) throws IOException {
		Put put = new Put(Bytes.toBytes(changeMD5(paper_id)));
		put.add(Bytes.toBytes("cite_info"), Bytes.toBytes("nCitation"), Bytes.toBytes(Citation_year));
		table3.put(put);
		table3.flushCommits();
	

	}

	public void insertKCIIF(String publisher_name, String score) throws IOException {
		Put put = new Put(Bytes.toBytes(changeMD5(publisher_name)));
		put.add(Bytes.toBytes("impact_factor"), Bytes.toBytes("score"), Bytes.toBytes(score));
		table4.put(put);
		table4.flushCommits();
	
	}

	public void insertPScore(String paper_id, String score) throws IOException {
		Put put = new Put(Bytes.toBytes(changeMD5(paper_id)));
		put.add(Bytes.toBytes("pscore"), Bytes.toBytes("score"), Bytes.toBytes(score));
		table5.put(put);
		table5.flushCommits();
	}

	public void close() throws IOException {
		table.close();
		table1.close();
		table2.close();
		table3.close();
		table4.close();
		table5.close();
		
		
	}
	public String changeMD5(String str){
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
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
}

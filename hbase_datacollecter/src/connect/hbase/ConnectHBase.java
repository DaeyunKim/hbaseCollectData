package connect.hbase;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import hbase_Test.paperInfo;

public class ConnectHBase {
	private static Configuration conf = null;
	private static HBaseAdmin admin=null;
	public static HTable table = null, table1, table2, table3, table4, table5, table6;

	public ConnectHBase() throws IOException {
		mkconfig();
		mkAdmin();
	//	System.out.println("success mkadmin");
		createTable();
//		System.out.println("success create Table");
		setHTable();

	}

	private static void mkconfig() {

		conf = HBaseConfiguration.create();
		conf.clear();
		conf.set("hbase.master", "127.0.0.1");
		conf.set("hbase.zookeeper.quorum", "127.0.0.1");

	}

	public static void mkAdmin() {

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

	public void setHTable() throws IOException {
		table.setAutoFlush(false);
		table.setWriteBufferSize(1024 * 1024 * 12);
		table1.setAutoFlush(false);
		table1.setWriteBufferSize(1024 * 1024 * 12);
		table2.setAutoFlush(false);
		table2.setWriteBufferSize(1024 * 1024 * 12);
		table3.setAutoFlush(false);
		table3.setWriteBufferSize(1024 * 1024 * 12);
		table4.setAutoFlush(false);
		table4.setWriteBufferSize(1024 * 1024 * 12);
		table5.setAutoFlush(false);
		table5.setWriteBufferSize(1024 * 1024 * 12);
		table6.setAutoFlush(false);
		table6.setWriteBufferSize(1024 * 1024 * 12);

	};

	public void exeFlushcommit() throws RetriesExhaustedWithDetailsException, InterruptedIOException {
		table.flushCommits();
		table1.flushCommits();
		table2.flushCommits();
		table3.flushCommits();
		table4.flushCommits();
		table5.flushCommits();
		table6.flushCommits();

	}

	public static void createTable() throws IOException {
		System.out.println("create table");
	
		if (!admin.tableExists("T_PAPER_INFO")) {
			System.out.println("----------creat table number! : -----------1-");
			HTableDescriptor H_T_TABLE = new HTableDescriptor("T_PAPER_INFO");
			H_T_TABLE.addFamily(new HColumnDescriptor("paper_info"));
			H_T_TABLE.addFamily(new HColumnDescriptor("issue_info"));
			H_T_TABLE.addFamily(new HColumnDescriptor("url"));
			H_T_TABLE.addFamily(new HColumnDescriptor("keyword"));
			admin.createTable(H_T_TABLE);
			table = new HTable(conf, "T_PAPER_INFO");
			System.out.println("----------creat table number! : ------------");
		} else {
			table = new HTable(conf, "T_PAPER_INFO");
			System.out.println("----------exist table number! : ------------");

		}

		System.out.println("admin.isTableAvailable2 ");
		
		
		if (!admin.isTableAvailable("T_EXPERT_INFO")) {
			HTableDescriptor H_T_EXPERT_INFO = new HTableDescriptor("T_EXPERT_INFO");
			H_T_EXPERT_INFO.addFamily(new HColumnDescriptor("paper_info"));
			admin.createTable(H_T_EXPERT_INFO);
			table1 = new HTable(conf, "T_EXPERT_INFO");
			System.out.println("Auto flush: " + table1.isAutoFlush());
		} else {
			table1 = new HTable(conf, "T_EXPERT_INFO");
			System.out.println("----------exist table number! : ------------");

		}

		if (!admin.isTableAvailable("T_KEYWORD_INFO")) {
			HTableDescriptor H_T_KEYWORD_INFO = new HTableDescriptor("T_KEYWORD_INFO");
			H_T_KEYWORD_INFO.addFamily(new HColumnDescriptor("paper_info"));
			admin.createTable(H_T_KEYWORD_INFO);
			table2 = new HTable(conf, "T_KEYWORD_INFO");
			System.out.println("Auto flush: " + table2.isAutoFlush());
		} else {
			table2 = new HTable(conf, "T_KEYWORD_INFO");
			System.out.println("----------exist table number! : ------------");

		}

		if (!admin.isTableAvailable("T_KCIIF_INFO")) {
			HTableDescriptor H_T_KCIIF_INFO = new HTableDescriptor("T_KCIIF_INFO");
			H_T_KCIIF_INFO.addFamily(new HColumnDescriptor("Impact_factor"));
			admin.createTable(H_T_KCIIF_INFO);
			table4 = new HTable(conf, "T_KCIIF_INFO");
		} else {
			table4 = new HTable(conf, "T_KCIIF_INFO");
			System.out.println("----------exist table number! : ------------");

		}

		if (!admin.isTableAvailable("MT_P_SCORE")) {
			HTableDescriptor H_MT_P_SCORE = new HTableDescriptor("MT_P_SCORE");
			H_MT_P_SCORE.addFamily(new HColumnDescriptor("pscore"));
			admin.createTable(H_MT_P_SCORE);
			table5 = new HTable(conf, "MT_P_SCORE");
		} else {
			table5 = new HTable(conf, "MT_P_SCORE");
			System.out.println("----------exist table number! : ------------");

		}
		if (!admin.isTableAvailable("T_RELATION_INFO")) {
			HTableDescriptor H_MT_P_SCORE = new HTableDescriptor("T_RELATION_INFO");
			H_MT_P_SCORE.addFamily(new HColumnDescriptor("with_who"));
			admin.createTable(H_MT_P_SCORE);
			table6 = new HTable(conf, "T_RELATION_INFO");
		} else {
			table6 = new HTable(conf, "T_RELATION_INFO");
			System.out.println("----------exist table number! : ------------");

		}

		if (!admin.isTableAvailable("T_PAPER_CITATION_INFO")) {
			HTableDescriptor H_T_PAPER_CITATION_INFO = new HTableDescriptor("T_PAPER_CITATION_INFO");
			H_T_PAPER_CITATION_INFO.addFamily(new HColumnDescriptor("cite_info"));
			admin.createTable(H_T_PAPER_CITATION_INFO);
			table3 = new HTable(conf, "T_PAPER_CITATION_INFO");
			System.out.println("Auto flush: " + table2.isAutoFlush());
		} else {
			table3 = new HTable(conf, "T_PAPER_CITATION_INFO");
			System.out.println("----------exist table number! : ------------");

		}
	}

	public void insertPaperInfo(String url, String title, String nAuthor, String Author_names, String issue_number,
			String issue_date, String issue_name, String publisher_name, String authorURL, String paperURL,
			String publisherURL, String keyword) throws IOException {
		// System.out.println(title + '*' + nAuthor + '*' + Author_names + '*' +
		// issue_number + '*' + issue_date + '*'
		// + issue_name + '*' + publisher_name + '*' +
		// authorURL+'*'+paperURL+'*'+publisherURL+'*'+keyword);
		Put put = new Put(Bytes.toBytes(transMD5(url)));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("title"), Bytes.toBytes(title));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("nAuthor"), Bytes.toBytes(nAuthor));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("Author_names"), Bytes.toBytes(Author_names));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_number"), Bytes.toBytes(issue_number));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_date"), Bytes.toBytes(issue_date));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("issue_name"), Bytes.toBytes(issue_name));
		put.add(Bytes.toBytes("issue_info"), Bytes.toBytes("publisher_name"), Bytes.toBytes(publisher_name));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("authorURL"), Bytes.toBytes(authorURL));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("paperURL"), Bytes.toBytes(paperURL));
		put.add(Bytes.toBytes("url"), Bytes.toBytes("publisherURL"), Bytes.toBytes(publisherURL));
		put.add(Bytes.toBytes("keyword"), Bytes.toBytes("keywords"), Bytes.toBytes(keyword));

		table.put(put);

	}

	public void insertExpertInfo(String nameKeyword, String paperId, String AuthorClassify)
			throws RetriesExhaustedWithDetailsException, InterruptedIOException {

		// cht.insertExpertInfo(crdb.getNum(paper.eachAuthor(),0).get(i) +
		// crdb.getNum(crdb.paper_keyword,
		// 0).get(0)+timestamp(),paper.linkURL,"0");// Author_classify
		Put put = new Put(Bytes.toBytes(transMD5(nameKeyword)));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("title"), Bytes.toBytes(paperId));
		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("nAuthor"), Bytes.toBytes(AuthorClassify));

		table1.put(put);

	}

	public void insertKeywordInfo(String keyword, String url)
			throws RetriesExhaustedWithDetailsException, InterruptedIOException {
		Put put = new Put(Bytes.toBytes(transMD5(keyword)));

		put.add(Bytes.toBytes("paper_info"), Bytes.toBytes("paper_id"), Bytes.toBytes(transMD5(url)));

		table2.put(put);

	}

	public void insertPaperCitationInfo(String paperId, String nCitation, String CitationYear) {

	}

	public void insertKCIIF(String publisher_name, String score) {

	}

	public void insertPScore(String paperId, float score) throws RetriesExhaustedWithDetailsException, InterruptedIOException {

		Put put = new Put(Bytes.toBytes(transMD5(paperId)));

		put.add(Bytes.toBytes("pscroe"), Bytes.toBytes("score"), Bytes.toBytes(score));

		table5.put(put);
		
	}

	// call names
	/*
	public void insertCountRelation(paperInfo pi) throws IOException {
		System.out.println("insert relation author info ");
		int cnt = 0;
		Result result = null;
		ArrayList<String> name = new ArrayList<String>();

		for (int i = 0; i < pi.author.size(); i++) {
			name.add(i, pi.author.get(i).name);
			System.out.println("name : " + pi.author.get(i).name);
		}

		for (int i = 0; i < pi.author.size(); i++) {

			String strTemp = pi.author.get(i).name;
			Put put = new Put(Bytes.toBytes(strTemp));
			Get get = new Get(Bytes.toBytes(name.get(i)));
			for (int j = 0; j < pi.author.size(); j++) {
				if (i != j) {
					get.addColumn(Bytes.toBytes("cf1"), Bytes.toBytes(name.get(j)));
				}
			}

			result = table6.get(get);

			for (int j = 0; j < pi.author.size(); j++) {
				if (i != j) {
					try {
						cnt = Bytes.toInt(result.getValue(Bytes.toBytes("cf1"), Bytes.toBytes(name.get(j))));
						cnt++;
					} catch (Exception e) {
						cnt = 0;
					}
					if (cnt == 0)
						cnt = 1;
					put.add(Bytes.toBytes("cf1"), Bytes.toBytes(name.get(j)), Bytes.toBytes(cnt));
					table6.put(put);

				}
			}
		}
	}
*/
	// call integer
	public void insertCountRelation(ArrayList<Integer> mappingNum) throws IOException {
		// TODO Auto-generated method stub
		int cnt = 0;
		Result result = null;
		ArrayList<String> name = new ArrayList<String>();

		for (int i = 0; i < mappingNum.size(); i++) {
			name.add(i, "name:" + mappingNum.get(i));
		}

		for (int i = 0; i < mappingNum.size(); i++) {

			String strTemp = name.get(i);
			Put put = new Put(Bytes.toBytes(strTemp));
			Get get = new Get(Bytes.toBytes(name.get(i)));
			for (int j = 0; j < mappingNum.size(); j++) {
				if (i != j) {
					get.addColumn(Bytes.toBytes("with_who"), Bytes.toBytes(name.get(j)));
				}
			}

			result = table6.get(get);

			for (int j = 0; j < mappingNum.size(); j++) {
				if (i != j) {
					try {
						cnt = Bytes.toInt(result.getValue(Bytes.toBytes("with_who"), Bytes.toBytes(name.get(j))));
						cnt++;
					} catch (Exception e) {
						cnt = 0;
					}
					if (cnt == 0)
						cnt = 1;
					put.add(Bytes.toBytes("with_who"), Bytes.toBytes(name.get(j)), Bytes.toBytes(cnt));
					table6.put(put);

				}
			}
		}
	}

	public String transMD5(String str) {

		String MD5 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();

			System.out.println(MD5);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			MD5 = null;
		}

		return MD5;

	}

}

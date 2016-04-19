package hbase_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import connect.hbase.ConnectHBase;
import connect.rdb.ConnectRDB;

public class source {
	int count = 0;
	Document doc = null;
	String add1 = "http://api.dbpia.co.kr/v1/search/search.xml?key=8cdcafebb30f172422d40f954d1cd40d&target=se_adv&searchall=";
	int number = 0;
	int pageNumber = 1;
	int categoryNum = 1;
	int authorURL = 0;
	//
	ConnectHBase cht = new ConnectHBase();
	ConnectRDB crdb = new ConnectRDB();
	GregorianCalendar today = new GregorianCalendar ( );
	int today_year=today.YEAR;
	public source() throws IOException, ParserConfigurationException, SAXException {

		System.out.println("검색어");
		getData();
		System.out.println("총 갯수 : " + number + " 카테고리 : " + (categoryNum - 1) + " 페이지수 : " + (pageNumber - 1));
		//System.out.println("url수 : " + authorURL);
	
	}

	public void getData() throws IOException, ParserConfigurationException, SAXException {

		Scanner sc = new Scanner(System.in);
		String keyword = sc.nextLine();

		this.add1 = add1 + keyword;

		System.out.println(add1);
		URL url = new URL(add1);
		InputStream in = url.openStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			String temp = line;
			response.append(temp);
			// System.out.println(temp);
			response.append('\r');
		}
		rd.close();
		getDocument(in, url);
		pageNumber(doc);
		System.out.println("Auto flush: " + cht.table.isAutoFlush());
		
		//pageNumber(doc)  page number
		for (pageNumber = 1; pageNumber <1+1; pageNumber++) {
					
			for (categoryNum = 1; categoryNum < 10; categoryNum++) {
				String url_page = add1 + "&pagenumber=" + pageNumber + "&category=" + categoryNum;
				url = new URL(url_page);
				getDocument(in, url);
				getItemData(doc, keyword);
			}
			
		}
		
		cht.exeFlushcommit();
		crdb.close();
	}

	public void getDocument(InputStream str, URL url) throws ParserConfigurationException, IOException, SAXException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputStream stream = str;
		stream = url.openStream();

		this.doc = db.parse(stream);

		this.doc.getDocumentElement().normalize();

	}

	@SuppressWarnings("deprecation")
	public void getItemData(Document doc, String keyword) throws IOException {

		NodeList itemNodeList = doc.getElementsByTagName("item");

		System.out.println("itemwjscptn  : " + itemNodeList.getLength());

		
		
		for (int i = 0; i < itemNodeList.getLength(); i++) {
			paperInfo paper = new paperInfo(keyword);
			Node itemNode = itemNodeList.item(i);
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
				Element itemElement = (Element) itemNode;
				getTitle(itemElement, paper);
				getAuthors(itemElement, paper);
				getPublisher(itemElement, paper);
				getIssue(itemElement, paper);
				getlinkURL(itemElement, paper);
				//setKeyWords(ArrayList<String> keywords);
				paper.paper_keyword.add(keyword);
				System.out.println("텍스트에 들어가게될 논문 정보 :: " + paper.callAuthor());
				cht.insertPaperInfo(paper.linkURL, paper.title,paper.transAuthorSize(), paper.callAuthor(),
						paper.Issue_number, paper.Issue_date, paper.Issue_name, paper.publisher_name, paper.callAuthorURL(),
						paper.linkURL, paper.publisher_url,keyword);// Ask
																		// about
													// keyword.?
				System.out.println("insert data success");
				
				//authors going to working 20160405--
				ArrayList<String> keywords = new ArrayList<String>();//get minsoo's ontology
				keywords.add(keyword);
				keywords.add("db");
				ArrayList<Integer> temp_author = crdb.getNum(paper.eachAuthor(),0);//Integer/ArrayList
				ArrayList<Integer> temp_keyword =crdb.getNum(keywords, 1);
				//FIX ME insertExpertInfo
				 System.out.println("paper.Author().size() : "+paper.author.size());
				 //System.out.println("paper.eachAuthor().size() : "+paper.eachAuthor().size());
				System.out.println("temp_author");
				for(int p:temp_author){
					
					System.out.println(p);
					
				}
				System.out.println("temp_keyword");
				for(int p:temp_keyword){
					
					System.out.println(p);
					
				}
				
				//first author insert main Author
				// create RDB 
				
				
				for(int q=0;q<temp_keyword.size();q++){
					
					for (int o = 0; o < temp_author.size(); o++) {
						if(o==0){
							cht.insertExpertInfo(temp_author.get(o)+"_"+ temp_keyword.get(q)+"_"+timestamp(),paper.linkURL,"1");			
							
						}else{
							cht.insertExpertInfo(temp_author.get(o)+"_" +temp_keyword.get(q)+"_"+timestamp(),paper.linkURL,"0");// Author_classify							
						}
						
					}
	
					
				}
								
/*
				for (int o = 1; i < paper.author.size(); o++) {
						for(int k=0;k<paper.paper_keyword.size();k++){
								cht.insertExpertInfo(crdb.getNum(paper.eachAuthor(),0).get(0) + crdb.getNum(crdb.paper_keyword, 0).get(k)+timestamp(),paper.linkURL,"0");// Author_classify		
						}
					

				}
*/
				
				//FIX ME insertKeywordInfo
				//samlpe input context=> keyword
				 cht.insertKeywordInfo(keyword, paper.linkURL);
				 
				//FIX ME PS
				//FIX ME citationInfo
				//FIX ME relationInfo
				//cht.insertCountRelation(paper);
				cht.insertCountRelation(temp_author);
				//FIX ME count keyword, count paper
				
				
		//		cht.insertKeywordInfo(keyword, paper.linkURL);

				// 입력받아야됨
			//	cht.insertKCIIF(paper.publisher_name, "score");
			//	cht.insertPScore("paper.linkURL", "score");
				
				//calculate P_SCORE
				//System.out.println("paper.Issue_date : "+paper.Issue_date);
				String hyear = paper.Issue_date.substring(0,3);
				Integer.parseInt(hyear);//paper Year
				int n_diff = today_year-Integer.parseInt(hyear);
				//currentYear
				 
				
				float score = (float) (1/(Math.log(2+n_diff))) ;
				cht.insertPScore(paper.linkURL,score);
				
				// 이부분 인용수
			//	cht.insertPaperCitationInfo(paper.linkURL, "nCitation", "Citation_year");

				
				
				
				
			}

			System.out.println("Author Size: " + paper.author.size());

		} 
	
		
	}


	public int pageNumber(Document doc) {
		NodeList itemNodeList = doc.getElementsByTagName("paramdata");
		Node itemNode = itemNodeList.item(0);
		Element itemElement = (Element) itemNode;
		NodeList pagecount = itemElement.getElementsByTagName("pagecount");
		Element pageElement = (Element) pagecount.item(0);
		NodeList childElementNodeList = pageElement.getChildNodes();
		System.out.println("pageTotalNumber : " + childElementNodeList.item(0));
		String ss = childElementNodeList.item(0).toString();

		int pageTotalNumber = Integer.parseInt(extractValue(ss));
		return pageTotalNumber;// �럹�씠吏� �닽�옄!!
	}

	// �젣紐�
	public void getTitle(Element itemElement, paperInfo paper) {

		String title = null;
		NodeList titlelist = itemElement.getElementsByTagName("title");
		Element titleElement = (Element) titlelist.item(0);
		NodeList childElementNodeList = titleElement.getChildNodes();
		title = extractValue(childElementNodeList.item(0).toString());
		String a = null;
		if (title.indexOf("<span") == -1) {

			paper.title = title;

		} else {

			paper.title = extractTitle(title);

		}

 

	}


	public void getAuthors(Element itemElement, paperInfo paper) {
		paper.author = new ArrayList<Author>();
		NodeList title = itemElement.getElementsByTagName("authors");

		if (title.getLength() != 0) {
			Element authorsElement = (Element) title.item(0);
			if (authorsElement.hasChildNodes() == true) {

				NodeList authorInfo = authorsElement.getElementsByTagName("author");
				for (int i = 0; i < authorInfo.getLength(); i++) {
					String order = null;
					String name = null;
					String url = null;
					Author author = new Author();
					Element authorElement = (Element) authorInfo.item(i);
					NodeList authorInfo2 = authorsElement.getElementsByTagName("order");
					Element authorElement2 = (Element) authorInfo2.item(i);
					NodeList authorInfo3 = authorsElement.getElementsByTagName("name");
					Element authorElement3 = (Element) authorInfo3.item(i);

					order = extractValue(authorElement2.getChildNodes().item(0).toString());

					name = extractValue(authorElement3.getChildNodes().item(0).toString());
					try {
						
						NodeList authorInfo1 = authorsElement.getElementsByTagName("url");
						Element authorElement1 = (Element) authorInfo1.item(i);
					//	System.out.println(authorElement1.getChildNodes().item(0).toString()==null);
						
						
							url = extractValue(authorElement1.getChildNodes().item(0).toString());	
						
						
						
					} catch (NullPointerException np) {

					
					}

					if (name.indexOf("<span") == -1) {
						author.name = name;
					} else {
						author.name = extractTitle(name);
					}

					author.order = order;

					if (url != null) {

						author.url = url;
						authorURL++;

					} else {
						author.url = null;
					}

					paper.author.add(author);

				}

			}

		}

	}

	// 諛쒗뻾湲곌� 李얘린!!
	public void getPublisher(Element itemElement, paperInfo paper) {

		String url = null;// 二쇱냼�씠由�
		String name = null;// �씠由�
		NodeList publisher = itemElement.getElementsByTagName("publisher");

		Element publisherElement = (Element) publisher.item(0);
		NodeList puble_url_list = publisherElement.getElementsByTagName("url");
		Element puble_url_ele = (Element) puble_url_list.item(0);
		NodeList puble_Name_list = publisherElement.getElementsByTagName("name");
		Element puble_Name_ele = (Element) puble_Name_list.item(0);
		url = extractValue(puble_url_ele.getChildNodes().item(0).toString());
		name = extractValue(puble_Name_ele.getChildNodes().item(0).toString());
		paper.publisher_url = url;
		paper.publisher_name = name;

	}

	public void getIssue(Element itemElement, paperInfo paper) {
		String name = null;
		String number = null;
		String date = null;

		NodeList issueList = itemElement.getElementsByTagName("issue");

		Element issueElement = (Element) issueList.item(0);
		NodeList issue_name_list = issueElement.getElementsByTagName("name");
		Element issue_name_ele = (Element) issue_name_list.item(0);
		NodeList issue_Num_list = issueElement.getElementsByTagName("num");
		Element issue_Num_ele = (Element) issue_Num_list.item(0);
		NodeList issue_date_list = issueElement.getElementsByTagName("yymm");
		Element issue_date_ele = (Element) issue_date_list.item(0);
		try {
			name = extractValue(issue_name_ele.getChildNodes().item(0).toString());
		} catch (NullPointerException npe) {
			name = "null";
		}


		try {
			number = extractValue(issue_Num_ele.getChildNodes().item(0).toString());
		} catch (NullPointerException npe) {
			number = "null";
		}


		try{
			date = extractValue(issue_date_ele.getChildNodes().item(0).toString());	
		} catch (NullPointerException npe) {
			date = "null";
		}
		

		paper.Issue_name = name;
		paper.Issue_number = number;
		paper.Issue_date = date;

	}

	public void getlinkURL(Element itemElement, paperInfo paper) {
		String linkurl = null;
		NodeList link_List = itemElement.getElementsByTagName("link_url");
		Element link_ele = (Element) link_List.item(0);
		linkurl = extractValue(link_ele.getChildNodes().item(0).toString());

		paper.linkURL = linkurl;
	}


	public String extractValue(String st) {
		String str = st;
		str = str.substring(8, str.length() - 1).trim();
		return str;
	}

	
	String extractTitle(String title) {
		// TODO 자동 생성된 메소드 스텁
		String result_str=title;
		String str=title;
		int first_loc=0;
		
			while(result_str.indexOf("<") >= 0) 
			{
				result_str="";
				
				if(str.indexOf("<") > 0)
				{
					result_str += str.substring(first_loc, str.indexOf("<"));
				}
				result_str += str.substring(str.indexOf(">")+1, str.length());	
				str = result_str;
				
			}
		
		return result_str;
	}
//time
	public String timestamp(){
		String time;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		
		 time= dateFormat.format(calendar.getTime());
		
		return time;
	}
	
	
}

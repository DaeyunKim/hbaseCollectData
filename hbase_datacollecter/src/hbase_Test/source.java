package hbase_Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
import ontology.UseOntology;

public class source {
	int count = 0;
	Document doc = null;
	String add1 = "http://api.dbpia.co.kr/v1/search/search.xml?pyear=3&pyear_start=2011&category=4&pyear_end=2016&key=8cdcafebb30f172422d40f954d1cd40d&target=se_adv&searchall=";

	int pageNumber = 1;
	int categoryNum = 1;
	int authorURL = 0;

	//
	ConnectHBase cht = new ConnectHBase();
	// ConnectHBase cht = null;
	ConnectRDB crdb = new ConnectRDB();
	Calendar today = Calendar.getInstance();
	int today_year = today.get(Calendar.YEAR);
	UseOntology uo = new UseOntology();// ontology keywords
	String searchkeyword[] = { "영상처리", "인공지능", "자연어처리", "소셜", "빅데이터", "네트워크", "정보보안", "데이터베이스", "센서", "온톨로지", "클라우드",
			"프로그래밍언어" };
	HashMap<String, int[]> keywordCount = new HashMap<String, int[]>();
	// keyword,year,count
	int year_count[][];// allocate each keyword 0:2011 ~5: 2016
	int sum_count[] = new int[6];

	public source() throws IOException, ParserConfigurationException, SAXException {

		// System.out.println("검색어");
		year_count = new int[12][6];// allocate each keyword 0:2011 ~5: 2016
		int kewordnumber[] = new int[12];
	//	// for (String keyword : keyword) {
		for (int i = 0; i < searchkeyword.length; i++) {
			
			getData(searchkeyword[i]);
			//getData("인공지능");
			
			System.out.println("총 갯수 : " + kewordnumber + " keyword : " + searchkeyword + " 페이지수 : " + (pageNumber - 1));

			// keywordCount.put(keyword, year_count);


		}
		
		
		for(int i=0;i<searchkeyword.length;i++){
			keywordCount.put(searchkeyword[i],year_count[i]);
			for (int j = 0; j < 6; j++) {
				sum_count[j] += year_count[i][j];
				kewordnumber[i]+=year_count[i][j];
			}
			
		}
		
		crdb.setKeywordPerYear(keywordCount);
		crdb.setPaperPerYear(sum_count);
		for (int s : sum_count) {

			System.out.println("each year sum paper : " + s);

		}
		for (int s : kewordnumber) {

			System.out.println("each keyword sum paper : " + s);

		}
		
		// System.out.println("keyword Count 2011:
		// "+keywordCount.get("영상처리")[0]);
		System.out.println("year_count");
		for(int i=0;i<searchkeyword.length;i++){
			keywordCount.put(searchkeyword[i],year_count[i]);
			for (int j = 0; j < 6; j++) {
				System.out.print(" "+ year_count[i][j]);
			}
			System.out.println();
		}
		
		crdb.close();
	}

	public void getData(String keyword) throws IOException, ParserConfigurationException, SAXException {
		/*
		 * Scanner sc = new Scanner(System.in); String keyword = sc.nextLine();
		 * this.add1=add1+keyword;
		 */
		String searchUrl = add1 + keyword;
		System.out.println(searchUrl);

		URL url = new URL(searchUrl);
		InputStream in = url.openStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(in));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			String temp = line;
			response.append(temp);
			// System.out.println("temp : "+temp);
			response.append('\r');
		}
		rd.close();
		getDocument(in, url);
		System.out.println("pageNumber(doc) : " + pageNumber(doc));
		;
		System.out.println("Auto flush: " + cht.table.isAutoFlush());

		// pageNumber(doc) page number
		for (pageNumber = 1; pageNumber < pageNumber(doc) + 1; pageNumber++) {

			String url_page = searchUrl + "&pagenumber=" + pageNumber;
			url = new URL(url_page);
			getDocument(in, url);
			System.out.println("pageNumber : " + pageNumber);
			NodeList itemNodeList = doc.getElementsByTagName("item");

			// System.out.println("itemNodeList.getLength() :
			// "+itemNodeList.getLength());
			int n = itemNodeList.getLength();
			if (n != 0) {
				// System.out.println("1page stop n : " + n + "
				// itemNodeList.getLength() : " + itemNodeList.getLength());
				getItemData(doc, keyword);
			} else {

				// System.out.println("2page stop n : " + n + "
				// itemNodeList.getLength() : " + itemNodeList.getLength());

			}
			System.out.println("page stop");
			cht.exeFlushcommit();
		}
		// System.out.println("flush1 stop");

		// System.out.println("flush2 stop");
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

		System.out.println("item  : " + itemNodeList.getLength());

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
				// setKeyWords(ArrayList<String> keywords);
				paper.paper_keyword.add(keyword);
				// System.out.println("텍스트에 들어가게될 논문 정보 :: " +
				// paper.callAuthor());
				cht.insertPaperInfo(paper.linkURL, paper.title, paper.transAuthorSize(), paper.callAuthor(),
						paper.Issue_number, paper.Issue_date, paper.Issue_name, paper.publisher_name,
						paper.callAuthorURL(), paper.linkURL, paper.publisher_url, keyword);// Ask
																							// about
				// keyword.?
				System.out.println("insert data success");

				// authors going to working 20160405--
				// insert keyword use Ontology
				ArrayList<String> keywords = uo.mkOntoKeywords(paper.title, keyword);// get minsoo's ontology

				ArrayList<Integer> temp_author = crdb.getNum(paper.eachAuthor(), 0);// Integer/ArrayList
				ArrayList<Integer> temp_keyword = crdb.getNum(keywords, 1);
				// FIX ME insertExpertInfo
				// System.out.println("paper.Author().size() :
				// "+paper.author.size());
				// System.out.println("paper.eachAuthor().size() :
				// "+paper.eachAuthor().size());
				// System.out.println("temp_author");
				/*
				 * for(int p:temp_author){
				 * 
				 * System.out.println(p);
				 * 
				 * } // System.out.println("temp_keyword"); for(int
				 * p:temp_keyword){
				 * 
				 * System.out.println(p);
				 * 
				 * }
				 */
				// first author insert main Author
				// create RDB

				for (int q = 0; q < temp_keyword.size(); q++) {

					for (int o = 0; o < temp_author.size(); o++) {
						if (o == 0) {
							cht.insertExpertInfo(temp_author.get(o) + "_" + temp_keyword.get(q) + "_" + timestamp(),
									paper.linkURL, "1");

						} else {
							cht.insertExpertInfo(temp_author.get(o) + "_" + temp_keyword.get(q) + "_" + timestamp(),
									paper.linkURL, "0");// Author_classify
						}

					}

				}

				/*
				 * for (int o = 1; i < paper.author.size(); o++) { for(int
				 * k=0;k<paper.paper_keyword.size();k++){
				 * cht.insertExpertInfo(crdb.getNum(paper.eachAuthor(),0).get(0)
				 * + crdb.getNum(crdb.paper_keyword,
				 * 0).get(k)+timestamp(),paper.linkURL,"0");// Author_classify }
				 * 
				 * 
				 * }
				 */

				// FIX ME insertKeywordInfo
				// samlpe input context=> keyword

				for (int q = 0; q < temp_keyword.size(); q++) {

					cht.insertKeywordInfo(temp_keyword.get(q) + "_" + timestamp(), paper.linkURL);

				}

				// FIX ME PS
				// FIX ME citationInfo
				// FIX ME relationInfo
				// cht.insertCountRelation(paper);
				cht.insertCountRelation(temp_author);
				// FIX ME count keyword, count paper

				// cht.insertKeywordInfo(keyword, paper.linkURL);

				// 입력받아야됨
				// cht.insertKCIIF(paper.publisher_name, "score");
				// cht.insertPScore("paper.linkURL", "score");

				// calculate P_SCORE
				String hyear = paper.Issue_date.substring(0, 4).trim();
				int hyear_i = Integer.parseInt(hyear);// paper Year
				System.out.println("paper IssueDate : " + hyear + " currentYear : " + today_year);
				int n_diff = today_year - hyear_i;
				float score = (float) (1 / (Math.log(2 + n_diff)));
				// year_count[keyword][year]
				
				
				for (int k = 0; k < keywords.size(); k++) {
					int keyword_number=0;
					for(int z=0;z<searchkeyword.length;z++){
						if(keywords.get(0).equals(searchkeyword[z])){
							keyword_number=z;
						}
					}
					switch (hyear_i) {
					case 2011:
						year_count[keyword_number][0]++;
						break;
					case 2012:
						year_count[keyword_number][1]++;
						break;
					case 2013:
						year_count[keyword_number][2]++;
						break;
					case 2014:
						year_count[keyword_number][3]++;
						break;
					case 2015:
						year_count[keyword_number][4]++;
						break;
					case 2016:
						year_count[keyword_number][5]++;
						break;
					}
				}

				cht.insertPScore(paper.linkURL, score);// edit 20160421

				// 이부분 인용수
				cht.insertPaperCitationInfo(paper.linkURL, "nCitation", "Citation_year");
				
			}
		
			// System.out.println("Author Size: " + paper.author.size());

		}

	}

	public void countNumber(String keyword, int year) {
		int count[] = new int[6];// allocate each keyword 0:2011 ~5: 2016

		switch (year) {
		case 2011:
			count[0]++;
			break;
		case 2012:
			count[1]++;
			break;
		case 2013:
			count[2]++;
			break;
		case 2014:
			count[3]++;
			break;
		case 2015:
			count[4]++;
			break;
		case 2016:
			count[5]++;
			break;

		}
		keywordCount.put(keyword, count);

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
						// System.out.println(authorElement1.getChildNodes().item(0).toString()==null);

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

		try {
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

		paper.linkURL = linkurl.replace("http://www.dbpia.co.kr/article/", " ");
	}

	public String extractValue(String st) {
		String str = st;
		str = str.substring(8, str.length() - 1).trim();
		return str;
	}

	String extractTitle(String title) {
		// TODO 자동 생성된 메소드 스텁
		String result_str = title;
		String str = title;
		int first_loc = 0;

		while (result_str.indexOf("<") >= 0) {
			result_str = "";

			if (str.indexOf("<") > 0) {
				result_str += str.substring(first_loc, str.indexOf("<"));
			}
			result_str += str.substring(str.indexOf(">") + 1, str.length());
			str = result_str;

		}

		return result_str;
	}

	// time
	public String timestamp() {
		String time;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

		time = dateFormat.format(calendar.getTime());

		return time;
	}

}

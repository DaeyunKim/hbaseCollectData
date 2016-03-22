package collectData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import hbaseconnect.createHTable;

public class source {
	int count = 0;
	Document doc = null;
	String add1 = "http://api.dbpia.co.kr/v1/search/search.xml?key=8cdcafebb30f172422d40f954d1cd40d&target=se_adv&searchall=";
	int number = 0;
	int pageNumber = 1;
	int categoryNum = 1;
	int authorURL = 0;

	public source() throws IOException, ParserConfigurationException, SAXException {

		System.out.println("검색어");
		getData();
		System.out.println("총 갯수 : " + number + " 카테고리 : " + (categoryNum - 1) + " 페이지수 : " + (pageNumber - 1));
		System.out.println("url수 : " + authorURL);
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
		for (pageNumber = 1; pageNumber < pageNumber(doc) + 1; pageNumber++) {
			for (categoryNum = 1; categoryNum < 10; categoryNum++) {
				String url_page = add1 + "&pagenumber=" + pageNumber + "&category=" + categoryNum;
				url = new URL(url_page);
				getDocument(in, url);
				getItemData(doc, keyword);
				// System.out.println("�럹�씠吏� �닔 : " + pageNumber);

			}

		}

	}

	// item蹂꾨줈 �뜲�씠�꽣 媛�吏�怨좎삤湲�
	public void getDocument(InputStream str, URL url) throws ParserConfigurationException, IOException, SAXException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		InputStream stream = str;
		stream = url.openStream();

		this.doc = db.parse(stream);

		this.doc.getDocumentElement().normalize();

	}

	// item�쑝濡� �굹�늿�뮘 title遺덈윭�삤湲�
	@SuppressWarnings("deprecation")
	public void getItemData(Document doc, String keyword) throws IOException {

		NodeList itemNodeList = doc.getElementsByTagName("item");

		System.out.println("itemwjscptn  : " + itemNodeList.getLength());// 媛��닔

		///////////////////////////////////////////////////////////////////
		createHTable cht = new createHTable();
		for (int i = 0; i < itemNodeList.getLength(); i++) {
			paperInfo paper = new paperInfo(keyword);// �끉臾� �븘�씠�뀥 媛앹껜

			Node itemNode = itemNodeList.item(i);
			if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
				Element itemElement = (Element) itemNode;

				// NodeList authors =
				// itemElement.getElementsByTagName("author");
				// System.out.println("authors 媛��닔 : "+authors.getLength());
				// ======
				getTitle(itemElement, paper);
				getAuthors(itemElement, paper);
				getPublisher(itemElement, paper);
				getIssue(itemElement, paper);
				// getfree_yn(itemElement,paper);
				// getPreview(itemElement,paper);
				getlinkURL(itemElement, paper);

				/// 湲곗꽌 �뵒鍮� paparInfo瑜� �꽔�뼱二쇰㈃�맖!!!
				System.out.println("텍스트에 들어가게될 논문 정보 :: " + paper.toString());// +paper.toString()
				cht.insertPaperInfo(paper.linkURL, paper.title, paper.author.size(), paper.callAuthor(),
						paper.Issue_number, paper.Issue_date, paper.Issue_name, paper.publisher_name, "authorURL",
						paper.linkURL, paper.publisher_url, "keywords");// Ask
																		// about
																		// keyword.?
				// authors
				for (int o = 0; i < paper.author.size(); o++) {
					cht.insertExpertInfo(paper.author.get(i) + keyword, paper.linkURL, "Author_classify");// Author_classify

				} // paper_id,

				cht.insertKeywordInfo(keyword, paper.linkURL);

				// 입력받아야됨
				cht.insertKCIIF(paper.publisher_name, "score");
				cht.insertPScore("paper.linkURL", "score");
				// 이부분 인용수
				cht.insertPaperCitationInfo(paper.linkURL, "nCitation", "Citation_year");

			}

			System.out.println("�끉臾� ���옄 �닽�옄 : " + paper.author.size());

		} // for臾몄씠 �걹�굹�뒗怨�!!

	}

	// �슂�빟�젙蹂�
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
			// System.out.println("span �뾾�쓬");
			paper.title = title;

		} else {
			// System.out.println("span �엳�쓬");
			// System.out.println("title1 : "+title);
			paper.title = extractTitle(title);
			// System.out.println("title1 : "+paper.title);
		}

		// System.out.println("title2 : " + paper.title);

	}

	// Authors�븞�쓽 �궡�슜!!
	public void getAuthors(Element itemElement, paperInfo paper) {
		paper.author = new ArrayList<Author>();
		NodeList title = itemElement.getElementsByTagName("authors");
		// System.out.println("authors size : " + title.getLength());
		if (title.getLength() != 0) {
			Element authorsElement = (Element) title.item(0);
			// System.out.println("authorsElement : " +
			// authorsElement.hasChildNodes());
			// System.out.println("authorsElement :
			// "+authorsElement.hasChildNodes());
			if (authorsElement.hasChildNodes() == true) {

				NodeList authorInfo = authorsElement.getElementsByTagName("author");
				// System.out.println("authors size : " +
				// authorInfo.getLength());//
				// ���옄
				// �닽�옄
				// 遺덈윭�삤湲�
				for (int i = 0; i < authorInfo.getLength(); i++) {
					String order = null;
					String name = null;
					String url = null;
					Author author = new Author();
					Element authorElement = (Element) authorInfo.item(i);
					// System.out.println("authorElement :
					// "+authorElement.hasChildNodes());
					NodeList authorInfo2 = authorsElement.getElementsByTagName("order");
					Element authorElement2 = (Element) authorInfo2.item(i);
					NodeList authorInfo3 = authorsElement.getElementsByTagName("name");
					Element authorElement3 = (Element) authorInfo3.item(i);

					order = extractValue(authorElement2.getChildNodes().item(0).toString());
					// System.out.println("order : " + order);// �닚�꽌
					name = extractValue(authorElement3.getChildNodes().item(0).toString());
					try {
						NodeList authorInfo1 = authorsElement.getElementsByTagName("url");
						Element authorElement1 = (Element) authorInfo1.item(i);
						url = extractValue(authorElement1.getChildNodes().item(0).toString());
						// System.out.println("autherUrl : " + url);// �닚�꽌
					} catch (NullPointerException np) {

						// np.printStackTrace();

					}

					if (name.indexOf("<span") == -1) {
						// System.out.println("span �뾾�쓬");
						author.name = name;
						// System.out.println("name2 : " + author.name);// �씠由�
					} else {
						// System.out.println("span �엳�쓬");
						// System.out.println("name1"+name);
						author.name = extractTitle(name);
						// System.out.println("name1 : "+author.name);
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
				// System.out.println("�끉臾� ���옄 �닽�옄 : "+paper.author.size());
			}

		}

	}

	// 諛쒗뻾湲곌� 李얘린!!
	public void getPublisher(Element itemElement, paperInfo paper) {

		String url = null;// 二쇱냼�씠由�
		String name = null;// �씠由�
		NodeList publisher = itemElement.getElementsByTagName("publisher");
		// System.out.println("authors size : " + title.getLength());
		Element publisherElement = (Element) publisher.item(0);
		NodeList puble_url_list = publisherElement.getElementsByTagName("url");
		Element puble_url_ele = (Element) puble_url_list.item(0);
		NodeList puble_Name_list = publisherElement.getElementsByTagName("name");
		Element puble_Name_ele = (Element) puble_Name_list.item(0);
		url = extractValue(puble_url_ele.getChildNodes().item(0).toString());
		name = extractValue(puble_Name_ele.getChildNodes().item(0).toString());
		// System.out.println("publisher_url : " + url);// �씠由�
		// System.out.println("publisher_name : " + name);// �씠由�
		paper.publisher_url = url;
		paper.publisher_name = name;

	}

	public void getIssue(Element itemElement, paperInfo paper) {
		String name = null;
		String number = null;
		String date = null;

		NodeList issueList = itemElement.getElementsByTagName("issue");
		// System.out.println("authors size : " + title.getLength());
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
			name = null;
		}

		// System.out.println("Issue_name : " + name);
		try {
			number = extractValue(issue_Num_ele.getChildNodes().item(0).toString());
		} catch (NullPointerException npe) {
			number = null;
		}

		// System.out.println("Issue_number : " + number);
		date = extractValue(issue_date_ele.getChildNodes().item(0).toString());
		// System.out.println("Issue_date : " + date);
		paper.Issue_name = name;
		paper.Issue_number = number;
		paper.Issue_date = date;

	}

	// 臾대즺 怨듦컻 �뿬遺�
	/*
	 * public void getfree_yn(Element itemElement, paperInfo paper) { String
	 * free = null; NodeList free_yn_List =
	 * itemElement.getElementsByTagName("free_yn"); Element free_yn_ele =
	 * (Element) free_yn_List.item(0); free =
	 * extractValue(free_yn_ele.getChildNodes().item(0).toString());
	 * System.out.println("臾대즺怨듦컻 �뿬遺� : " + free); paper.free_yn=free;
	 * 
	 * }
	 */
	/*
	 * // �봽由ы벂 �젣怨� �뿬遺� public void getPreview(Element itemElement, paperInfo
	 * paper) { String preview_url = null; String preview_yn = null;
	 * 
	 * NodeList preview_List = itemElement.getElementsByTagName("preview_yn");
	 * System.out.println("preview_ele childNodes: " +
	 * preview_List.getLength());
	 * 
	 * // System.out.println("preview_ele childNodes: "
	 * +preview_ele.hasChildNodes()); if (preview_List.getLength() == 1) {
	 * Element preview_ele = (Element) preview_List.item(0); preview_yn =
	 * extractValue(preview_ele.getChildNodes().item(0) .toString());
	 * System.out.println("preview_YN : " + preview_yn); if
	 * (preview_yn.equals("Y")) { preview_List =
	 * itemElement.getElementsByTagName("preview"); preview_ele = (Element)
	 * preview_List.item(0); preview_url =
	 * extractValue(preview_ele.getChildNodes().item(0) .toString());
	 * System.out.println("preview_URL : " + preview_url);
	 * 
	 * } else {
	 * 
	 * preview_url = null; System.out.println("preview_URL : " + preview_url); }
	 * } paper.preview_url = preview_url; paper.preview_yn=preview_yn;
	 * 
	 * }
	 */
	public void getlinkURL(Element itemElement, paperInfo paper) {
		String linkurl = null;
		NodeList link_List = itemElement.getElementsByTagName("link_url");
		Element link_ele = (Element) link_List.item(0);
		linkurl = extractValue(link_ele.getChildNodes().item(0).toString());
		// System.out.println("留곹겕 URL : " + linkurl);
		paper.linkURL = linkurl;
	}

	// xml->�뜲�씠�꽣 �닔�젙!!
	public String extractValue(String st) {
		String str = st;
		str = str.substring(8, str.length() - 1).trim();
		return str;
	}

	public String extractTitle(String title) {
		String a;
		int start1, end1;
		int start2, end2;
		start1 = title.indexOf("<");
		end1 = title.indexOf(">");
		start2 = 0;
		end2 = 0;
		for (int i = title.length(); i > 8; i--) {

			if (title.substring(i - 7, i).equals("</span>")) {
				start2 = i - 7;
				end2 = i;
			}

		}
		a = title.substring(0, start1);
		a = a + title.substring(end1 + 1, start2);
		a = a + title.substring(end2, title.length());

		return a;
	}

}

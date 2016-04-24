package ontology;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.kaist.swrc.jhannanum.exception.ResultTypeException;

public class UseOntology {

	static String WORDFILE = "word.txt";
	static String ONTOLOGYFILE = "KeyWordOntology.owl";
	static String NS = "http://netdb/#";
	CreateOntology co = null;
	Morpheme m = null;
	SearchOntology so = null;
	ArrayList<String> resultKeywords = new ArrayList<String>();
	ArrayList<String> wordlist = new ArrayList<String>();;

	public UseOntology() throws IOException {

		co = new CreateOntology(NS, WORDFILE, ONTOLOGYFILE);
		m = new Morpheme();
		so = new SearchOntology(NS, ONTOLOGYFILE);
	}

	public ArrayList<String> mkOntoKeywords(String title, String keyword) throws IOException {
		resultKeywords.clear();
		wordlist.clear();
		System.out.println("paper name : " + title);
		try {
			wordlist = m.workflowAnalyze(title);
		} catch (ResultTypeException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

		// 온톨로지 검색을 위한 클래
		so = new SearchOntology(NS, ONTOLOGYFILE);
		// 키워드 추출하는 함수
		// System.out.println("키워드 추출하는 함수");

		System.out.println("wordlist.size() : " + wordlist.size());
		if (wordlist.size() != 0) {
			int n = 1;
			for (String s : wordlist) {
				// System.out.println(s);
				s = s.toUpperCase();
				// System.out.println(so.SearchKeyWord(s));
				if (resultKeywords.contains(so.SearchKeyWord(s))) {

				} else if (so.SearchKeyWord(s) == null && n == wordlist.size()) {
					//System.out.println("n : " + n);
					
					if(resultKeywords.contains(so.SearchKeyWord(s))){
						resultKeywords.add(keyword);
					}
					
				} else if (so.SearchKeyWord(s) != null) {
					resultKeywords.add(so.SearchKeyWord(s));
				}
				n++;
			}
		}
		
		for(String s:resultKeywords){
			
			System.out.println(s);
		}
		
		return resultKeywords;

	}

}

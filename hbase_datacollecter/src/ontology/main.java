package ontology;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.ac.kaist.swrc.jhannanum.exception.ResultTypeException;

public class main {

	/**
	 * @param args
	 * @throws IOException
	 */
	static String WORDFILE = "word.txt";
	static String ONTOLOGYFILE = "KeyWordOntology.owl";
	static String NS = "http://netdb/#";

	public static void main(String[] args) throws IOException {
		// TODO 자동 생성된 메소드 스텁

		// 온톨로지 생성.
		CreateOntology co = new CreateOntology(NS, WORDFILE, ONTOLOGYFILE);

		// 형태소 분석기.
		Morpheme m = new Morpheme();
		ArrayList<String> wordlist = new ArrayList<String>();
		try {
			wordlist = m.workflowAnalyze("Proxy Mobile IPv6 네트워크에서 패킷 유실을 방지하는 끊김 없는 멀티캐스트 기법");
		} catch (ResultTypeException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

		// 온톨로지 검색을 위한 클래
		SearchOntology so = new SearchOntology(NS, ONTOLOGYFILE);

		System.out.println("키워드 추출하는 함수");
		// 키워드 추출하는 함수
		if (wordlist.size() != 0) {

			for (String s : wordlist) {
				// System.out.println(s);
				s = s.toUpperCase();
				System.out.println(so.SearchKeyWord(s));
			}
		}
		
		/*
		// 키워드로부터 하위 서브 클래스들 추출하는 함수
		System.out.println("키워드로부터 하위 서브 클래스들 추출하는 함수");
		ArrayList<String> list = new ArrayList<String>();
		list = so.SearchMember("빅데이터");
		if (list != null) {
			for (String s : list) {
				System.out.println(s);
			}
		}
*/
	}
}

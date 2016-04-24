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
			//사전 과제를 활용한 프로그래밍 언어 교수 학습 모델 설계
			//원격 디밍제어 기반 LED 가시광통신 시스템의 수신 특성 분석
			wordlist = m.workflowAnalyze("원격 디밍제어 기반 LED 가시광통신 시스템의 수신 특성 분석");
		} catch (ResultTypeException e) {
			// TODO 자동 생성된 catch 블록
			e.printStackTrace();
		}

		// 온톨로지 검색을 위한 클래
		SearchOntology so = new SearchOntology(NS, ONTOLOGYFILE);

		ArrayList<String> resultKeywords = new ArrayList<String>();
		System.out.println("wordlist.size() : " + wordlist.size());
		if (wordlist.size() != 0) {
			int n = 1;
			for (String s : wordlist) {
				// System.out.println(s);
				s = s.toUpperCase();
				// System.out.println(so.SearchKeyWord(s));
				if (resultKeywords.contains(so.SearchKeyWord(s))) {

				} else if (so.SearchKeyWord(s) == null && n == wordlist.size()) {
					System.out.println("n : " + n);
					System.out.println("resultKeywords.contains(so.SearchKeyWord(s)) : " +resultKeywords.contains(so.SearchKeyWord(s)));
					
					if(resultKeywords.contains(so.SearchKeyWord(s))){
						resultKeywords.add(so.SearchKeyWord(s));
					}
					
				} else if (so.SearchKeyWord(s) != null) {
					resultKeywords.add(so.SearchKeyWord(s));
				}
				n++;
			}
		}


		System.out.println(resultKeywords.size());
		;
		for (String s : resultKeywords) {

			System.out.println(s);
			;
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

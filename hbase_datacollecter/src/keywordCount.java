import java.util.ArrayList;
import java.util.HashMap;

public class keywordCount {

	HashMap<String, int[]> keywordCount = new HashMap<String, int[]>();

	public void countNumber(String keyword) {
		int count[] = new int[6];// allocate each keyword 0:2011 ~5: 2016
		int count1[]={0,};//init
	
		int year = 0;

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

}

package melwin.spamfilter.mailprocessor;

import java.util.HashMap;

import melwin.spamfilter.main.AllMaps;
import melwin.spamfilter.main.Utils;

public class ActionSpamProbability extends ActionClass{
	private static boolean DEBUG_ACTION_CALC_SPAM_PROB = Utils.DEBUG_ALL | false;
	private HashMap<String, Integer> map;
	
	public ActionSpamProbability() {
		this.map = AllMaps.getSpam();
	}
	
	@Override
	public double performAction(String token, int weight) {
		double prob=-1;
		int total_words = AllMaps.getSpamTotalWords();
		if(map.containsKey(token)){
			int word_count = map.get(token);
			prob = (double)word_count/total_words;
			if(DEBUG_ACTION_CALC_SPAM_PROB) System.out.println("-> \""+token+"\" } "+ "{"+prob+":("+word_count+"/"+total_words+")} EXISTING TOKEN ");
			
		}else{
			int word_count = 1;
			prob = (double)word_count/total_words;
			if(DEBUG_ACTION_CALC_SPAM_PROB) System.out.println("-> \""+token+"\" } "+ "{"+prob+":("+word_count+"/"+total_words+")} NEW TOKEN ");
		}
		return prob;
	}

}

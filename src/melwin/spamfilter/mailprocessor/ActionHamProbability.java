package melwin.spamfilter.mailprocessor;

import java.util.HashMap;

import melwin.spamfilter.main.AllMaps;

public class ActionHamProbability extends ActionClass{
	private static boolean DEBUG_ACTION_CALC_SPAM_PROB = true;
	private HashMap<String, Integer> map;
	
	public ActionHamProbability() {
		this.map = AllMaps.getHam();
	}
	
	@Override
	public double performAction(String token, int weight) {
		double prob=-1;
		if(map.containsKey(token)){
			int word_count = map.get(token);
			int total_words = AllMaps.getHamTotalWords();
			prob = (double)word_count/total_words;
			if(DEBUG_ACTION_CALC_SPAM_PROB) System.out.println("-> \""+token+"\" } "+ "{"+prob+":("+word_count+"/"+total_words+")} EXISTING TOKEN ");
		}else{
			int word_count = 1;
			int total_words = AllMaps.getHamTotalWords();
			prob = (double)word_count/total_words;
			if(DEBUG_ACTION_CALC_SPAM_PROB) System.out.println("-> \""+token+"\" } "+ "{"+prob+":("+word_count+"/"+total_words+")} NEW TOKEN ");
		}
		return prob;
	}

}

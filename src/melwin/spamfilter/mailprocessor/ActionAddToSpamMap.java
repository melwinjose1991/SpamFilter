package melwin.spamfilter.mailprocessor;

import java.util.HashMap;

import melwin.spamfilter.main.AllMaps;

public class ActionAddToSpamMap extends ActionClass{
	private HashMap<String, Integer> map;
	private static boolean DEBUG_ACTION_ON_SPAM_MAP = true;
	
	public ActionAddToSpamMap(){
		this.map = AllMaps.getSpam();
	}
	
	@Override
	public void performAction(String processedToken, int weight) {
		if(map.containsKey(processedToken)){
			int old_val = map.get(processedToken);
			map.put(processedToken, old_val+weight);
			if(DEBUG_ACTION_ON_SPAM_MAP)
				System.out.println("-> \""+processedToken+"\" } "+ "{"+old_val+"->"+(old_val+weight)+"} EXISTING TOKEN ");
		}else{
			map.put(processedToken, weight);
			if(DEBUG_ACTION_ON_SPAM_MAP)
				System.out.println("-> \""+processedToken+"\" } "+ "{"+weight+"} NEW TOKEN ");
		}
		AllMaps.incrementSpamTotalWords();
	}

}

package melwin.spamfilter.main;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AllMaps {
	private static HashMap<String, Integer> spam;
	private static HashMap<String, Integer> ham;
	private static Set<String> commonWords;

	public static void initAllMaps() {
		spam = new HashMap<String, Integer>();
		ham = new HashMap<String, Integer>();
		commonWords = new HashSet<String>();
		initCommonWords();
	}

	private static void initCommonWords() {
		// TODO : read common words from commom words file
		commonWords.add("why");
		commonWords.add("more");
	}

	public static Set<String> getCommonWords() {
		return commonWords;
	}

	public static HashMap<String, Integer> getSpam() {
		return spam;
	}

	public static HashMap<String, Integer> getHam() {
		return ham;
	}

}

package melwin.spamfilter.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AllMaps {
	private static final boolean DEBUG_COMMON_WORDS_MAP = false;
	
	private static HashMap<String, Integer> spam;
	private static int spamTotalWords=0;
	private static HashMap<String, Integer> ham;
	private static int hamTotalWords=0;
	private static Set<String> commonWords;

	public static void initAllMaps() throws IOException {
		spam = new HashMap<String, Integer>();
		ham = new HashMap<String, Integer>();
		commonWords = new HashSet<String>();
		initCommonWords();
	}

	private static void initCommonWords() throws IOException {
		File fin = new File("resources\\common_words.txt");
		FileInputStream fis = new FileInputStream(fin);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		while ((line = br.readLine()) != null) {
			if(line.trim().length() == 0) continue;
			if(line.charAt(0)=='#') continue;	//comments in resources file
			if(DEBUG_COMMON_WORDS_MAP) System.out.println(line);
			commonWords.add(line.toLowerCase());
		}
		br.close();
		fis.close();
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

	public static int getSpamTotalWords() {
		return spamTotalWords;
	}

	public static void incrementSpamTotalWords() {
		AllMaps.spamTotalWords++;
	}

	public static int getHamTotalWords() {
		return hamTotalWords;
	}

	public static void incrementHamTotalWords() {
		AllMaps.hamTotalWords++;
	}

}

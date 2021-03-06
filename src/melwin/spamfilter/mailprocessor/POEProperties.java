package melwin.spamfilter.mailprocessor;

// Class : Parts Of Email Properties
public class POEProperties {
	public static final String SUBJECT_HEADER = "Subject";
	public static final int SUBJECT_WIEGHT = 4;
	public static final String SUBJECT_SPLIT_REGEX = " |-|/|\\.|-|,|:";
	public static final boolean SUBJECT_CONVERT_TO_LOWER = true;
	
	public static final String FROM_HEADER = "FROM";
	public static final int FROM_WIEGHT = 10;
	public static final String FROM_SPLIT_REGEX = " |<|>";
	public static final boolean FROM_CONVERT_TO_LOWER = true;
	
	public static final String RECEIVED_HEADER = "Received";
	public static final int RECEIVED_WIEGHT = 100;
	public static final String RECEIVED_SPLIT_REGEX = " |\\[|\\]|<|>|\\(|\\)|\\\r\\\n";
	public static final boolean RECEIVED_CONVERT_TO_LOWER = true;
	
	public static final String BODY_HEADER = "Body";
	public static final int BODY_WIEGHT = 1;
	public static final String BODY_SPLIT_REGEX = " |-|/|\\.|-|,|:|\\\r\\\n|\\n";
	public static final boolean BODY_CONVERT_TO_LOWER = true;

}

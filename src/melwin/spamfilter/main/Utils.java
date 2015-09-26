package melwin.spamfilter.main;

public class Utils {
	public static final int NO_RETURN = -1;
	
	public static final int NOT_SPAM = 0;
	public static final int IS_SPAM = 1;

	public static final String DNSBL_KEY = "dbjttelgoxgd";
	public static final String DNSBL_DOMAIN = "sbl.spamhaus.org";//"dnsbl.httpbl.org";
	
	public static final int TOTAL_SPAM_MAILS = 2200;
	public static final int SPAM_FOR_TRAINING = 2000;
	
	public static final int TOTAL_HAM_MAILS = 5700;
	public static final int HAM_FOR_TRAINING = 5700;
	
	public static final boolean DEBUG_ALL = false;
}

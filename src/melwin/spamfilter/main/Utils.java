package melwin.spamfilter.main;

public class Utils {
	public static int NO_RETURN = -1;
	
	public static int NOT_SPAM = 0;
	public static int IS_SPAM = 1;

	public static final String DNSBL_KEY = "dbjttelgoxgd";
	public static final String DNSBL_DOMAIN = "sbl.spamhaus.org";//"dnsbl.httpbl.org";
	
	public static int TOTAL_MAILS = 100;
	public static int FOR_TRAINING = 80;
}

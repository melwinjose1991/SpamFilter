package melwin.spamfilter.mailprocessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import melwin.spamfilter.main.Utils;

public class ActionCheckBL extends ActionClass {
	private static boolean DEBUG_ACTION_CHECK_BL = true;
	private Set<String> okIPs, blackListedIPs;

	private String accessKey;
	private String domain;
	private String lookup;

	public ActionCheckBL() {
		accessKey = Utils.DNSBL_KEY;
		domain = Utils.DNSBL_DOMAIN;
		blackListedIPs = new HashSet<String>();
		okIPs = new HashSet<String>();
	}

	@Override
	public double performAction(String token, int weight) {
		if(DEBUG_ACTION_CHECK_BL) System.out.print("\t=>IP : "+token+" ");
		if(okIPs.contains(token)){
			if(DEBUG_ACTION_CHECK_BL) System.out.println("from SET, NON-SPAM IP ");
			return Utils.NOT_SPAM;
		}
		if(blackListedIPs.contains(token)){
			if(DEBUG_ACTION_CHECK_BL) System.out.println("from SET, SPAM IP !!!");
			return Utils.IS_SPAM;
		}
		
		String reversedIp = reversed(token); 
		lookup = reversedIp + "." + domain;
		try {
			String result = InetAddress.getByName(lookup).getHostAddress();
			//if(DEBUG_ACTION_CHECK_BL) System.out.println("@@@ From server : "+result+" @@@");
			if(result.equals("127.0.0.2") || result.equals("127.0.0.3")){
				if(DEBUG_ACTION_CHECK_BL) System.out.println("from "+domain+", SPAM IP !!! [[[SPAM_BLACKLISTED]]]");
				blackListedIPs.contains(token);
				return Utils.IS_SPAM;
			}
		} catch (UnknownHostException e) {
			if(DEBUG_ACTION_CHECK_BL) System.out.println("from "+domain+", NON-SPAM IP");
			okIPs.add(token);
			return Utils.NOT_SPAM;
		} catch (Exception e) {
			return Utils.NOT_SPAM;
		}
		 return Utils.NOT_SPAM;
	}

	private static String reversed(String ip) {
		//System.out.println(" IP : "+ip+" ");
		String[] tokens = ip.split("\\.");
		String reversed = null;
		for (String token : tokens)
			reversed = (reversed == null) ? token : (token + "." + reversed);
		//System.out.println(" Reveresed IP : "+reversed+" ");
		return reversed;
	}

}

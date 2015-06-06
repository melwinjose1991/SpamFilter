package melwin.spamfilter.mailprocessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.main.Utils;

public class ProcessReceived extends Processor {

	private static final boolean DEBUG_RECEIVED_PROCESSOR = true;
	
	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static Pattern pattern;
	private static Matcher matcher;

	public ProcessReceived() {
		setHeader(POEProperties.RECEIVED_HEADER);
		setWeight(POEProperties.RECEIVED_WIEGHT);
		setSpliter(POEProperties.RECEIVED_SPLIT_REGEX);
		setConvertToLower(POEProperties.RECEIVED_CONVERT_TO_LOWER);
		
		pattern = Pattern.compile(IPADDRESS_PATTERN);
	}
	
	@Override
	public double process(MimeMessage email, ActionClass action) {
		try {
			double res=0;
			String values[] = email.getHeader(getHeader());
			if(values == null )  return Utils.NOT_SPAM;	// no received(s) header
			
			for(String value : values){
				if(DEBUG_RECEIVED_PROCESSOR) System.out.println("Received : {"+value+"}");
				String tokens[] = value.split(getSpliter());
				
				for(String token : tokens){
					if(token.trim().length()==0) continue;
					String processedToken = processToken(token);
					if(validToken(processedToken)){
						// check against DNS-BL
						if(DEBUG_RECEIVED_PROCESSOR) System.out.print("\t{ "+token+" - "+processedToken+" } IP\n");
						res = action.performAction(processedToken, getWeight());
						if(res==Utils.IS_SPAM){
							 return Utils.IS_SPAM;
						}
					}else{
						 if(DEBUG_RECEIVED_PROCESSOR)
							;//System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "+"COMMON WORD/INVALID TOKEN");
					}
				}
			}
			 return Utils.NOT_SPAM;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return Utils.NOT_SPAM;
	}
	
	@Override
	public boolean validToken(String token) {
		String input = token.trim();
		if (input.length() < 8) // only IPs
			return false;
		if(input.equals("127.0.0.1"))
			return false;
		matcher = pattern.matcher(token);
		return matcher.matches();	   
	}
	
	@Override
	public String processToken(String token) {
		return token.trim();
	}

}

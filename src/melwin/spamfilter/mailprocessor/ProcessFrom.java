package melwin.spamfilter.mailprocessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.main.Utils;

public class ProcessFrom extends Processor {
	private static final boolean DEBUG_FROM_PROCESSOR = Utils.DEBUG_ALL | false;
	
	Pattern pattern;
	Matcher matcher;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	

	public ProcessFrom() {
		setHeader(POEProperties.FROM_HEADER);
		setSpliter(POEProperties.FROM_SPLIT_REGEX);
		setWeight(POEProperties.FROM_WIEGHT);
		setConvertToLower(POEProperties.FROM_CONVERT_TO_LOWER);
		
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	@Override
	public double process(MimeMessage email, ActionClass action) {
		double res=1, temp;
		try {
			String froms[] = email.getHeader(getHeader());
			
			for(String from : froms){
				if(DEBUG_FROM_PROCESSOR) System.out.println("FROM : {"+from+"}");
				String tokens[] = from.split(getSpliter());
				
				for(String token : tokens){
					if(token.trim().length()==0) continue;
					String processedToken = processToken(token);
					if(validToken(processedToken)){
						if(DEBUG_FROM_PROCESSOR) System.out.print("\t{"+token+"}");
						matcher = pattern.matcher(token);
						if (matcher.find()) {
							temp=action.performAction(token, getWeight());
							if(DEBUG_FROM_PROCESSOR) System.out.println("\t<< CONTAINS EMAIL ID >>");
							if(temp==Utils.NO_RETURN){
								// return value not to be evaluated, 
								// i.e when training is going on
							}else{
								// when testing on sample data or actual new i/p
								res = res * temp;
							}
						}else{
							if(DEBUG_FROM_PROCESSOR) System.out.println("  << NOT EMAIL ID >>");
						}
					}else{
						 if(DEBUG_FROM_PROCESSOR)
							;//System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "+"COMMON WORD/INVALID TOKEN");
					}
					
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	@Override
	public String processToken(String token) {
		return token.trim();
	}
}

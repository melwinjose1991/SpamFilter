package melwin.spamfilter.mailprocessor;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;

import melwin.spamfilter.main.AllMaps;
import melwin.spamfilter.main.Utils;

public class ProcessBody extends Processor{
	private static final boolean DEBUG_BODY_PROCESSOR = Utils.DEBUG_ALL | false;
	
	public ProcessBody() {
		setHeader(POEProperties.BODY_HEADER);
		setWeight(POEProperties.BODY_WIEGHT);
		setSpliter(POEProperties.BODY_SPLIT_REGEX);
		setConvertToLower(POEProperties.BODY_CONVERT_TO_LOWER);
	}
	
	@Override
	public double process(MimeMessage email, ActionClass action) {
		try {
			double res=1, temp;
			Object object = email.getContent();
			if(object == null ){
				if(DEBUG_BODY_PROCESSOR) System.out.println("email.getContent() is NULL");
				return 1;		// no Body header(s), this probability shouldn't effect other probabilities
			}
			if(!(object instanceof String)){
				if(DEBUG_BODY_PROCESSOR) System.out.println("email.getContent() is NOT String");
				return 1;
			}
				
			if(DEBUG_BODY_PROCESSOR) System.out.print("BODY : ");
			String nonHTML = Jsoup.parse(object.toString()).text();
			String tokens[] = nonHTML.split(getSpliter());
			if(DEBUG_BODY_PROCESSOR) System.out.println("\t No.of.Tokens:"+tokens.length);
				
			for(String token : tokens){	// loop through each token of a Subject header
				if(token.trim().length()==0) continue;
				String processedToken = processToken(token);
					
				// ignoring numbers, blanks and commons words
				if(validToken(processedToken) && !AllMaps.getCommonWords().contains(processedToken)){
					if(DEBUG_BODY_PROCESSOR) System.out.print("\t{"+token+" ");
					temp = action.performAction(processedToken, getWeight());
					if(temp==Utils.NO_RETURN){
						// return value not to be evaluated, i.e when training is going on
						// here word is added to map
					}else{
						// when testing on sample data or actual new i/p
						// here the probability is returned
						res = res * temp;
					}
				}else{
					 if(DEBUG_BODY_PROCESSOR)
						;//System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "+"COMMON WORD/INVALID TOKEN");
				}
			}
			if(res==0.0){	
				System.out.println("<<< Double UnderFlow >>>");
				return Double.MIN_VALUE;
			}
			return res;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}

}

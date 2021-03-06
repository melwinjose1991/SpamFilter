package melwin.spamfilter.mailprocessor;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.main.AllMaps;
import melwin.spamfilter.main.Utils;

public class ProcessSubject extends Processor {
	private static final boolean DEBUG_SUBJECT_PROCESSOR = Utils.DEBUG_ALL | false;

	public ProcessSubject() {
		setHeader(POEProperties.SUBJECT_HEADER);
		setWeight(POEProperties.SUBJECT_WIEGHT);
		setSpliter(POEProperties.SUBJECT_SPLIT_REGEX);
		setConvertToLower(POEProperties.SUBJECT_CONVERT_TO_LOWER);
	}

	@Override
	public double process(MimeMessage email, ActionClass action) {
		try {
			double res=1, temp;
			String values[] = email.getHeader(getHeader());
			if(values == null ) return 0;		// no subject header(s)
			
			for(String value : values){	// loop through all Subject headers
				
				if(DEBUG_SUBJECT_PROCESSOR) System.out.println("Subject : {"+value+"}");
				String tokens[] = value.split(getSpliter());
				
				for(String token : tokens){	// loop through each token of a Subject header
					if(token.trim().length()==0) continue;
					String processedToken = processToken(token);
					
					// ignoring numbers, blanks and commons words
					if(validToken(processedToken) && !AllMaps.getCommonWords().contains(processedToken)){
						if(DEBUG_SUBJECT_PROCESSOR) System.out.print("\t{"+token+" ");
						temp = action.performAction(processedToken, getWeight());
						if(temp==Utils.NO_RETURN){
							// return value not to be evaluated, 
							// i.e when training is going on
							// here word is added to map
						}else{
							// when testing on sample data or actual new i/p
							// here the probability is returned
							res = res * temp;
						}
					}else{
						 if(DEBUG_SUBJECT_PROCESSOR)
							;//System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "+"COMMON WORD/INVALID TOKEN");
					}
				}
			}
			return res;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}

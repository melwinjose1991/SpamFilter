package melwin.spamfilter.mailprocessor;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.main.AllMaps;

public class ProcessSubject extends Processor {
	private static final boolean DEBUG_SUBJECT_PROCESSOR = true;

	public ProcessSubject() {
		setHeader(POEProperties.SUBJECT_HEADER);
		setWeight(POEProperties.SUBJECT_WIEGHT);
		setSpliter(POEProperties.SUBJECT_SPLIT_REGEX);
		setConvertToLower(POEProperties.SUBJECT_CONVERT_TO_LOWER);
	}

	@Override
	public void process(MimeMessage email, ActionClass action) {
		try {
			String values[] = email.getHeader(getHeader());
			if(values == null ) return;		// no subject(s) header
			
			for(String value : values){
				if(DEBUG_SUBJECT_PROCESSOR) System.out.println("Subject : {"+value+"}");
				String tokens[] = value.split(getSpliter());
				
				for(String token : tokens){
					if(token.trim().length()==0) continue;
					String processedToken = processToken(token);
					if(validToken(processedToken) && !AllMaps.getCommonWords().contains(processedToken)){
						System.out.print("{"+token+" ");
						action.performAction(processedToken, getWeight());
					}else{
						 if(DEBUG_SUBJECT_PROCESSOR)
							System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "+"COMMON WORD/INVALID TOKEN");
					}
				}
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

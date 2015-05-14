package melwin.spamfilter.mailprocessor;

import java.util.HashMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.main.AllMaps;

public class SubjectProcessor extends BaseProcessor {
	private static final boolean DEBUG_SUBJECT_PROCESSOR = true;

	public SubjectProcessor() {
		setHeader(POEProperties.SUBJECT_HEADER);
		setWeight(POEProperties.SUBJECT_WIEGHT);
		setSpliter(POEProperties.SUBJECT_SPLIT_REGEX);
		setConvertToLower(POEProperties.SUBJECT_CONVERT_TO_LOWER);
	}

	@Override
	public void process(MimeMessage email, HashMap<String, Integer> map) {
		try {
			String values[] = email.getHeader(getHeader());
			if(values == null ) return;		// no subject(s) header
			
			for(String value : values){
				if(DEBUG_SUBJECT_PROCESSOR) System.out.println("Subject : {"+value+"}");
				String tokens[] = value.split(getSpliter());
				
				for(String token : tokens){
					String processedToken = processToken(token);
					if(validToken(processedToken) && !AllMaps.getCommonWords().contains(processedToken)){
						if(map.containsKey(processedToken)){
							int old_val = map.get(processedToken);
							map.put(processedToken, old_val+getWeight());
							if(DEBUG_SUBJECT_PROCESSOR)
								System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "
										+ "{"+old_val+"->"+(old_val+getWeight()+"} EXISTING TOKEN "));
						}else{
							map.put(processedToken, getWeight());
							if(DEBUG_SUBJECT_PROCESSOR)
								System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "
										+ "{"+getWeight()+"} NEW TOKEN ");
						}
					}else{
						/*
						 if(DEBUG_SUBJECT_PROCESSOR)
						 
							System.out.println("{ \""+token+"\" -> \""+processedToken+"\" } "
									+ "COMMON WORD/INVALID TOKEN");
						*/
					}
				}
				
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

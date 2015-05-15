package melwin.spamfilter.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.mailprocessor.ActionClass;
import melwin.spamfilter.mailprocessor.ActionAddToHamMap;
import melwin.spamfilter.mailprocessor.ActionAddToSpamMap;
import melwin.spamfilter.mailprocessor.Processor;
import melwin.spamfilter.mailprocessor.ProcessSubject;

public class MainClass {
	public static void main(String[] args) {
		try {
			AllMaps.initAllMaps();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		MimeMessage email;
		Processor baseProcessor = new ProcessSubject();
		
		EmailLoader spamEailLoader = new FileEmailLoader();
		spamEailLoader.init("corpus-classified\\spam",10);
		EmailLoader hamEailLoader = new FileEmailLoader();
		hamEailLoader.init("corpus-classified\\ham",10);
		
		ActionClass addToSpam = new ActionAddToSpamMap();
		ActionClass addToHam = new ActionAddToHamMap();
		try {

			// learning spam email
			while ((email = spamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToSpam);
			}
			
			// learning ham email
			while ((email = hamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToHam);
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

package melwin.spamfilter.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.mailprocessor.BaseProcessor;
import melwin.spamfilter.mailprocessor.SubjectProcessor;

public class MainClass {
	public static void main(String[] args) {
		try {
			AllMaps.initAllMaps();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		MimeMessage email;
		BaseProcessor baseProcessor = new SubjectProcessor();
		EmailLoader spamEailLoader = new FileEmailLoader();
		spamEailLoader.init("corpus-classified\\spam",10);
		EmailLoader hamEailLoader = new FileEmailLoader();
		hamEailLoader.init("corpus-classified\\ham",10);
		try {

			while ((email = spamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, AllMaps.getSpam());
			}
			
			while ((email = hamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, AllMaps.getHam());
			}

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

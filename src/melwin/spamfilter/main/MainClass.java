package melwin.spamfilter.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Session;
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

		File[] mailFiles = new File[1];
		mailFiles[0] = new File("testing\\00001.7848dde101aa985090474a91ec93fcf0");
		String host = "host.com";
		java.util.Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		Session session = Session.getDefaultInstance(properties);
		for (File tmpFile : mailFiles) {
			MimeMessage email = null;
			try {
				FileInputStream fis = new FileInputStream(tmpFile);
				email = new MimeMessage(session, fis);
				BaseProcessor baseProcessor = new SubjectProcessor();
				baseProcessor.process(email, AllMaps.getSpam());
			} catch (MessagingException e) {
				throw new IllegalStateException("illegal state issue", e);
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("file not found issue issue: "
						+ tmpFile.getAbsolutePath(), e);
			}
		}

	}

}

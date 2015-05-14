package melwin.spamfilter.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class FileEmailLoader extends EmailLoader {

	private Session session;
	private int noOfEmails;
	private int index = 0;
	private File[] mailFiles;

	@Override
	public void init(String path, int max_limit) {
		File folder = new File(path);
		mailFiles = folder.listFiles();
		if (max_limit <= mailFiles.length) {
			noOfEmails = max_limit;
		} else {
			noOfEmails = mailFiles.length;
		}

		String host = "host.com";
		java.util.Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		session = Session.getDefaultInstance(properties);
		if(DEBUG_EMAIL_LOADER) System.out.println(" noOfEmails:"+noOfEmails+"\tmax_limit:"+max_limit);
	}

	@Override
	public MimeMessage getNextEmail() throws FileNotFoundException,
			MessagingException {

		if (index >= noOfEmails)
			return null;
		if(DEBUG_EMAIL_LOADER) System.out.println("FILE["+index+"]"+mailFiles[index].getName());
		FileInputStream fis = new FileInputStream(mailFiles[index++]);
		return new MimeMessage(session, fis);

	}

}

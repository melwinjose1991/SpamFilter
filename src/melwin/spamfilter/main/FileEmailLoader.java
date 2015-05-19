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
	private int training;
	private boolean isTraining;

	@Override
	public void init(String path, int max_limit, int training) {
		File folder = new File(path);
		mailFiles = folder.listFiles();
		if (max_limit <= mailFiles.length) {
			noOfEmails = max_limit;
		} else {
			noOfEmails = mailFiles.length;
		}
		this.isTraining = true;
		this.training = training;

		String host = "host.com";
		java.util.Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", host);
		session = Session.getDefaultInstance(properties);
		if(DEBUG_EMAIL_LOADER) System.out.println(" noOfEmails:"+noOfEmails+"\tmax_limit:"+max_limit+"\ttraining:"+training);
	}

	@Override
	public MimeMessage getNextEmail() throws FileNotFoundException,
			MessagingException {

		if (index >= noOfEmails)
			return null;
		
		if(isTraining && index >= training){
			if(DEBUG_EMAIL_LOADER) System.out.println("\n<<< FINISHED TRAINING >>>");
			isTraining = false;
			return null;
		}
		
		if(DEBUG_EMAIL_LOADER) System.out.println("\nFILE["+index+"]"+mailFiles[index].getName());
		FileInputStream fis = new FileInputStream(mailFiles[index]);
		index++;
		return new MimeMessage(session, fis);

	}

	public boolean isTraining() {
		return isTraining;
	}

}

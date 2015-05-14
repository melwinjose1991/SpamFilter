package melwin.spamfilter.main;

import java.io.FileNotFoundException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public abstract class EmailLoader {
	protected boolean DEBUG_EMAIL_LOADER = true;
	public abstract void init(String path, int max_limit);
	public abstract MimeMessage getNextEmail() throws FileNotFoundException, MessagingException;
}

package melwin.spamfilter.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.mailprocessor.ActionAddToHamMap;
import melwin.spamfilter.mailprocessor.ActionAddToSpamMap;
import melwin.spamfilter.mailprocessor.ActionClass;
import melwin.spamfilter.mailprocessor.ActionHamProbability;
import melwin.spamfilter.mailprocessor.ActionSpamProbability;
import melwin.spamfilter.mailprocessor.ProcessSubject;
import melwin.spamfilter.mailprocessor.Processor;

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
		spamEailLoader.init("corpus-classified\\spam", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		EmailLoader hamEailLoader = new FileEmailLoader();
		hamEailLoader.init("corpus-classified\\ham", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		
		ActionClass addToSpam = new ActionAddToSpamMap();
		ActionClass addToHam = new ActionAddToHamMap();
		ActionClass spamProb = new ActionSpamProbability();
		ActionClass hamProb = new ActionHamProbability();
		double p_spam, p_ham, P_spam, P_ham;
		try {

			// learning spam email
			while ((email = spamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToSpam);
			}
			
			// learning ham email
			while ((email = hamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToHam);
			}
			
			// testing spam email
			System.out.println("\n\n<<< Started classifying sample SPAM emails >>>");
			while ((email = spamEailLoader.getNextEmail()) != null) {
				p_spam = baseProcessor.process(email, spamProb);
				p_ham = baseProcessor.process(email, hamProb);
				P_spam = p_spam / (p_spam+p_ham);
				P_ham = p_ham / (p_spam+p_ham);
				System.out.println("P(SPAM)="+P_spam+"\tP(HAM)="+P_ham);
				if(P_spam > P_ham){
					System.out.println("SPAM, good job !!!");
				}else{
					System.out.println("Oh shit !!! this was a SPAM");
				}
			}
			
			// testing ham email
			System.out.println("\n\n<<< Started classifying sample HAM emails >>>");
			while ((email = hamEailLoader.getNextEmail()) != null) {
				p_spam = baseProcessor.process(email, spamProb);
				p_ham = baseProcessor.process(email, hamProb);
				P_spam = p_spam / (p_spam + p_ham);
				P_ham = p_ham / (p_spam + p_ham);
				System.out.println("P(SPAM)="+P_spam+"\tP(HAM)="+P_ham);
				if (P_spam > P_ham) {
					System.out.println("Oh shit !!! this was not SPAM");
				} else {
					System.out.println("HAM, good job !!!");
				}
			}
			

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

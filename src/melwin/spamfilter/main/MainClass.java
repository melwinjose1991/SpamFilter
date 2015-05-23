package melwin.spamfilter.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.rmi.CORBA.Util;

import melwin.spamfilter.mailprocessor.ActionAddToHamMap;
import melwin.spamfilter.mailprocessor.ActionAddToSpamMap;
import melwin.spamfilter.mailprocessor.ActionClass;
import melwin.spamfilter.mailprocessor.ActionHamProbability;
import melwin.spamfilter.mailprocessor.ActionSpamProbability;
import melwin.spamfilter.mailprocessor.ProcessFrom;
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
		Processor fromProcessor = new ProcessFrom();
		
		EmailLoader spamEailLoader = new FileEmailLoader();
		spamEailLoader.init("corpus-classified\\spam", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		EmailLoader hamEailLoader = new FileEmailLoader();
		hamEailLoader.init("corpus-classified\\ham", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		
		ActionClass addToSpam = new ActionAddToSpamMap();
		ActionClass addToHam = new ActionAddToHamMap();
		ActionClass spamProb = new ActionSpamProbability();
		ActionClass hamProb = new ActionHamProbability();
		double p_spam_subject, p_spam_from, p_spam;
		double p_ham_subject, p_ham_from, p_ham; 
		double P_spam, P_ham;
		
		int failedSpamClassifications[] = new int[Utils.TOTAL_MAILS-Utils.FOR_TRAINING];
		int failedSpamClassificationsSize=0;
		
		int failedHamClassifications[] = new int[Utils.TOTAL_MAILS-Utils.FOR_TRAINING];
		int failedHamClassificationsSize=0;
		try {

			// learning spam email
			while ((email = spamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToSpam);
				fromProcessor.process(email, addToSpam);
			}
			
			// learning ham email
			while ((email = hamEailLoader.getNextEmail()) != null) {
				baseProcessor.process(email, addToHam);
				fromProcessor.process(email, addToHam);
			}
			
			// testing spam email
			System.out.println("\n\n<<< Started classifying sample SPAM emails >>>");
			while ((email = spamEailLoader.getNextEmail()) != null) {
				p_spam_subject = baseProcessor.process(email, spamProb);
				p_ham_subject = baseProcessor.process(email, hamProb);
				
				p_spam_from = fromProcessor.process(email, spamProb);
				p_ham_from = fromProcessor.process(email, hamProb);
				
				p_spam = p_spam_from * p_spam_subject;
				p_ham = p_ham_from * p_ham_subject;
				P_spam = p_spam / (p_spam+p_ham);
				P_ham = p_ham / (p_spam+p_ham);
				System.out.println("P(SPAM)="+P_spam+"\tP(HAM)="+P_ham);
				if(P_spam > P_ham){
					System.out.println("SPAM, good job !!!");
				}else{
					System.out.println("Oh shit !!! this was a SPAM");
					failedSpamClassifications[failedSpamClassificationsSize++] = Integer.parseInt(spamEailLoader.getCurrentMailId()); 
				}
			}
			
			System.out.print("\n\n<<< FAILED SPAM CLASSIFICATIONS >>> : { ");
			for(int i=0;i<failedSpamClassificationsSize;i++){
				System.out.print(failedSpamClassifications[i]+",");
			}
			System.out.println(" }");
			
			// testing ham email
			System.out.println("\n\n<<< Started classifying sample HAM emails >>>");
			while ((email = hamEailLoader.getNextEmail()) != null) {
				p_spam_subject = baseProcessor.process(email, spamProb);
				p_ham_subject = baseProcessor.process(email, hamProb);
				
				p_spam_from = fromProcessor.process(email, spamProb);
				p_ham_from = fromProcessor.process(email, hamProb);
				
				p_spam = p_spam_from * p_spam_subject;
				p_ham = p_ham_from * p_ham_subject;
				P_spam = p_spam / (p_spam+p_ham);
				P_ham = p_ham / (p_spam+p_ham);
				System.out.println("P(SPAM)="+P_spam+"\tP(HAM)="+P_ham);
				if(P_ham > P_spam){
					System.out.println("HAM, good job !!!");
				}else{
					System.out.println("Oh shit !!! this was a HAM");
					failedHamClassifications[failedHamClassificationsSize++] = Integer.parseInt(hamEailLoader.getCurrentMailId()); 
				}
			}
			
			System.out.print("\n\n<<< FAILED HAM CLASSIFICATIONS >>> : { ");
			for(int i=0;i<failedHamClassificationsSize;i++){
				System.out.print(failedHamClassifications[i]+",");
			}
			System.out.println(" }");

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

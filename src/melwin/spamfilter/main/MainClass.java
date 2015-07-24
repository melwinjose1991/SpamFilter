package melwin.spamfilter.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import melwin.spamfilter.mailprocessor.ActionAddToHamMap;
import melwin.spamfilter.mailprocessor.ActionAddToSpamMap;
import melwin.spamfilter.mailprocessor.ActionCheckBL;
import melwin.spamfilter.mailprocessor.ActionClass;
import melwin.spamfilter.mailprocessor.ActionHamProbability;
import melwin.spamfilter.mailprocessor.ActionSpamProbability;
import melwin.spamfilter.mailprocessor.POEProperties;
import melwin.spamfilter.mailprocessor.ProcessBody;
import melwin.spamfilter.mailprocessor.ProcessFrom;
import melwin.spamfilter.mailprocessor.ProcessReceived;
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
		Processor subjectProcessor = new ProcessSubject();
		Processor fromProcessor = new ProcessFrom();
		Processor receivedProcessor = new ProcessReceived();
		Processor bodyProcessor = new ProcessBody();
		
		EmailLoader spamEailLoader = new FileEmailLoader();
		spamEailLoader.init("corpus-classified\\spam", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		EmailLoader hamEailLoader = new FileEmailLoader();
		hamEailLoader.init("corpus-classified\\ham", Utils.TOTAL_MAILS, Utils.FOR_TRAINING);
		
		ActionClass addToSpam = new ActionAddToSpamMap();
		ActionClass addToHam = new ActionAddToHamMap();
		ActionClass spamProb = new ActionSpamProbability();
		ActionClass hamProb = new ActionHamProbability();
		ActionClass checkBL = new ActionCheckBL();
		double p_spam_subject, p_spam_from, p_spam_received , p_spam_body, p_spam;
		double p_ham_subject, p_ham_from, p_ham_body, p_ham; 
		double P_spam, P_ham;
		
		int failedSpamClassifications[] = new int[Utils.TOTAL_MAILS-Utils.FOR_TRAINING];
		int failedSpamClassificationsSize=0;
		
		int failedHamClassifications[] = new int[Utils.TOTAL_MAILS-Utils.FOR_TRAINING];
		int failedHamClassificationsSize=0;
		
		int ipClassifications[] = new int[Utils.TOTAL_MAILS-Utils.FOR_TRAINING];
		int ipClassificationsSize=0;
		try {

			// learning spam email
			while ((email = spamEailLoader.getNextEmail()) != null) {
				subjectProcessor.process(email, addToSpam);
				fromProcessor.process(email, addToSpam);
				bodyProcessor.process(email, addToSpam);
			}
			// learning ham email
			while ((email = hamEailLoader.getNextEmail()) != null) {
				subjectProcessor.process(email, addToHam);
				fromProcessor.process(email, addToHam);
				bodyProcessor.process(email, addToHam);
			}
			

			// testing spam email
			System.out.println("\n\n<<< Started classifying sample SPAM emails >>>");
			while ((email = spamEailLoader.getNextEmail()) != null) {
				p_spam_subject = subjectProcessor.process(email, spamProb);
				p_ham_subject = subjectProcessor.process(email, hamProb);
				
				p_spam_from = fromProcessor.process(email, spamProb);
				p_ham_from = fromProcessor.process(email, hamProb);
				
				p_ham_body = bodyProcessor.process(email, hamProb);
				p_spam_body = bodyProcessor.process(email, spamProb);
				
				p_spam_received = receivedProcessor.process(email, checkBL);
				if(p_spam_received==1){
					ipClassifications[ipClassificationsSize++]= Integer.parseInt(spamEailLoader.getCurrentMailId()); 
				}
				
				System.out.println(" SPAM : Subject:"+p_spam_subject+"\tFrom:"+p_spam_from+"\tBody:"+p_spam_body+" Received:"+p_spam_received);
				System.out.println(" HAM : Subject:"+p_ham_subject+"\tFrom:"+p_ham_from+"\tBody:"+p_ham_body);
				
				p_spam = (p_spam_from * p_spam_subject * p_spam_body)+(p_spam_received==1?POEProperties.RECEIVED_WIEGHT:0);
				if(p_spam == Double.NaN || p_spam==0) p_spam = Double.MIN_VALUE;
				
				p_ham = p_ham_from * p_ham_subject * p_ham_body;
				if(p_ham == Double.NaN || p_ham==0) p_ham = Double.MIN_VALUE;
				
				System.out.println(" p_spam:"+p_spam+"\tp_ham:"+p_ham);
				
				// Problem : p_spam = (p_spam_from * p_spam_subject * p_spam_body)
				// if either of the term in the LHS is less than minimum value for Double then
				// overall value of p_spam becomes 0 or NaN
				// If this happens, p_spam and p_ham comparison would not be valid
				if(p_ham == Double.MIN_VALUE && p_spam == Double.MIN_VALUE){
					int spam_OR_ham=0;
					if(p_spam_body < p_ham_body) spam_OR_ham++; else if(p_spam_body > p_ham_body) spam_OR_ham--; 
					if(p_spam_from < p_ham_from) spam_OR_ham++; else if(p_spam_from > p_ham_from) spam_OR_ham--;
					if(p_spam_subject < p_ham_subject) spam_OR_ham++; else if(p_spam_subject > p_ham_subject) spam_OR_ham--;
					if(spam_OR_ham < 0){
						System.out.println("SPAM, good job !!! [SPECIAL HACK]");
					}else{
						System.out.println("Oh shit !!! this was a SPAM [SPECIAL HACK]");
						failedSpamClassifications[failedSpamClassificationsSize++] = Integer.parseInt(spamEailLoader.getCurrentMailId()); 
					}
					
				}else{
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
			}
			
			System.out.print("\n\n<<< FAILED SPAM CLASSIFICATIONS >>> : { ");
			for(int i=0;i<failedSpamClassificationsSize;i++){
				System.out.print(failedSpamClassifications[i]+",");
			}
			System.out.println(" } TOTAL:"+failedSpamClassificationsSize+"/"+(Utils.TOTAL_MAILS-Utils.FOR_TRAINING)+" WRONG CLASSIFICATIONS");
			
			System.out.print("\n\n<<< SPAM CLASSIFICATIONS USING IP BLACK LIST>>> : { ");
			for(int i=0;i<ipClassificationsSize;i++){
				System.out.print(ipClassifications[i]+",");
			}
			System.out.println(" }");

			/*
			// testing ham email
			System.out.println("\n\n<<< Started classifying sample HAM emails >>>");
			while ((email = hamEailLoader.getNextEmail()) != null) {
				p_spam_subject = subjectProcessor.process(email, spamProb);
				p_ham_subject = subjectProcessor.process(email, hamProb);
				
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
			*/

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

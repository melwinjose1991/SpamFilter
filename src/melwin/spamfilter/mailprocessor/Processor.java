package melwin.spamfilter.mailprocessor;

import javax.mail.internet.MimeMessage;

public abstract class Processor {
	private String header;
	private String spliter = " ";
	private int weight = 1;
	private boolean convertToLower = true;

	public abstract double process(MimeMessage email, ActionClass action);

	public boolean validToken(String token) {
		String input = token.trim();
		if (input.length() <= 0)
			return false;
		try {
			Double.parseDouble(input);
			return false;
		} catch (NumberFormatException nfe) {
			return true;
		}
	}

	public String processToken(String token) {
		token = isConvertToLower() ? token.toLowerCase() : token;
		token = token.replaceAll("[^A-Za-z0-9]", "");
		token = token.trim();
		return token;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getSpliter() {
		return spliter;
	}

	public void setSpliter(String spliter) {
		this.spliter = spliter;
	}

	public boolean isConvertToLower() {
		return convertToLower;
	}

	public void setConvertToLower(boolean convertToLower) {
		this.convertToLower = convertToLower;
	}
}

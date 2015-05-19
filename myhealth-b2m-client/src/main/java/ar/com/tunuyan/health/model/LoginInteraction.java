package ar.com.tunuyan.health.model;

import java.io.Serializable;
import java.util.Map;

public class LoginInteraction implements Serializable {

	private static final long serialVersionUID = -7683674109052305668L;

	private String nextAction;
	private Map<String, String> cookies;
	private String saml;
	private String secretQuestion;
	private String secretAnswer;

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public String getNextAction() {
		return nextAction;
	}

	public void setNextAction(String nextAction) {
		this.nextAction = nextAction;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public String getSaml() {
		return saml;
	}

	public void setSaml(String saml) {
		this.saml = saml;
	}

	public String getSecretQuestion() {
		return secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

}

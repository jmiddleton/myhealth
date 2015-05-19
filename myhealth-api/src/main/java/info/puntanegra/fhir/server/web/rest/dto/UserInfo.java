package info.puntanegra.fhir.server.web.rest.dto;

/**
 * User info DTO used to transfer info between the UI and the server.
 * 
 * @author jmiddleton
 *
 */
public class UserInfo {
	private String username;
	private String password;
	private String secretAnswer;
	private String secretQuestion;

	public UserInfo() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public String getSecretQuestion() {
		return this.secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

}

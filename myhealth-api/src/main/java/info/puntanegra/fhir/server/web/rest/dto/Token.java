package info.puntanegra.fhir.server.web.rest.dto;

import java.util.Date;

public class Token {
	private String token;
	private Date expires;

	public Token(String token) {
		this.token = token;
		this.expires = new Date(System.currentTimeMillis() + (1000 * 60 * 60)); // 1hour
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

}

package info.puntanegra.fhir.server.security.hmac;

import info.puntanegra.fhir.server.domain.User;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class UserAuthentication implements Authentication {

	private final User user;
	private boolean authenticated = true;

	public UserAuthentication(User user) {
		this.user = user;
	}

	@Override
	public String getName() {
		return user.getFirstName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//return user.getAuthorities();
		return null;
	}

	@Override
	public Object getCredentials() {
		return user.getPassword();
	}

	@Override
	public User getDetails() {
		return user;
	}

	@Override
	public Object getPrincipal() {
		return user.getLogin();
	}

	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}
}

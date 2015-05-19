package info.puntanegra.fhir.server.security.xauth;

import info.puntanegra.fhir.server.security.hmac.StatelessAuthenticationFilter;
import info.puntanegra.fhir.server.security.hmac.TokenAuthenticationService;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class XAuthTokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private TokenProvider tokenProvider;

	private UserDetailsService detailsService;

	private TokenAuthenticationService tokenAuthenticationService;

	public XAuthTokenConfigurer(UserDetailsService detailsService, TokenProvider tokenProvider,
			TokenAuthenticationService tokenAuthenticationService2) {
		this.detailsService = detailsService;
		this.tokenProvider = tokenProvider;
		this.tokenAuthenticationService = tokenAuthenticationService2;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// XAuthTokenFilter customFilter = new XAuthTokenFilter(detailsService,
		// tokenProvider);
		// http.addFilterBefore(customFilter,
		// UsernamePasswordAuthenticationFilter.class);

		StatelessAuthenticationFilter filter = new StatelessAuthenticationFilter(tokenAuthenticationService);
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
	}
}

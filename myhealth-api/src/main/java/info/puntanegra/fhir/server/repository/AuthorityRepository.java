package info.puntanegra.fhir.server.repository;

import info.puntanegra.fhir.server.domain.Authority;

import org.springframework.stereotype.Service;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
@Service
public class AuthorityRepository {

	public Authority findOne(String string) {
		Authority authority = new Authority();
		authority.setName("ROLE_USER");
		return authority;
	}
}

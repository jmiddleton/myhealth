package info.puntanegra.fhir.server.repository;

import info.puntanegra.fhir.server.domain.User;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Service
public class UserRepository {

	@Inject
	private CacheManager cacheManager;

	Cache cache;

	@PostConstruct
	public void init() {
		cache = cacheManager.getCache(this.getClass().getSimpleName());
	}

	public Optional<User> findOneByActivationKey(String activationKey) {
//		for (User user : this.userCache.values()) {
//			if (user.getActivationKey().equals(activationKey)) {
//				return Optional.of(user);
//			}
//		}
		return Optional.empty();
	}

	public List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime) {
		return null;
	}

	public Optional<User> findOneByEmail(String email) {
		return null;
	}

	public Optional<User> findOneByLogin(String login) {
		User user = this.cache.get(getLogin(login), User.class);
		if (user == null) {
			return Optional.empty();
		}
		return Optional.of(user);
	}

	private String getLogin(String login) {
		return login.toUpperCase();
	}

	public void delete(User user) {
		this.cache.evict(getLogin(user.getLogin()));
	}

	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(User user) {
		this.cache.put(getLogin(user.getLogin()), user);
	}

}

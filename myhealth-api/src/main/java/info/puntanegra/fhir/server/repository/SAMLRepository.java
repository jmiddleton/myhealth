package info.puntanegra.fhir.server.repository;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import ar.com.tunuyan.health.json.XmlToJsonConverter;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Service
public class SAMLRepository {

	@Inject
	private CacheManager cacheManager;

	Cache cache;

	@PostConstruct
	public void init() {
		cache = cacheManager.getCache(this.getClass().getSimpleName());

		try {
			cache.put("WG395514", XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return cache.get(key, String.class);
	}

	public void delete(String key) {
		this.cache.evict(key);
	}

	public void save(String key, String saml) {
		this.cache.put(key, saml);
	}

}

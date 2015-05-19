package info.puntanegra.fhir.server.config;

import java.util.ArrayList;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.google.code.ssm.Cache;
import com.google.code.ssm.CacheFactory;
import com.google.code.ssm.config.AddressProvider;
import com.google.code.ssm.config.DefaultAddressProvider;
import com.google.code.ssm.providers.spymemcached.MemcacheClientFactoryImpl;
import com.google.code.ssm.spring.SSMCache;
import com.google.code.ssm.spring.SSMCacheManager;

@Configuration
@EnableCaching
@Profile(Constants.SPRING_PROFILE_CLOUD)
public class MemcachedConfiguration {

	private final Logger log = LoggerFactory.getLogger(MemcachedConfiguration.class);

	@Inject
	private Environment env;

	@Bean
	public CacheManager cacheManager() {
		log.debug("Memcached Cache Manager");
		String address = env.getProperty("cache.memcached.ip", String.class, "127.0.0.1");
		Integer port = env.getProperty("cache.memcached.port", Integer.class, 11211);

		MemcacheClientFactoryImpl cacheClientFactory = new MemcacheClientFactoryImpl();
		AddressProvider addressProvider = new DefaultAddressProvider(address + ":" + port);
		com.google.code.ssm.providers.CacheConfiguration cacheConfiguration = new com.google.code.ssm.providers.CacheConfiguration();
		cacheConfiguration.setConsistentHashing(true); // TODO check this

		ArrayList<SSMCache> ssmCaches = new ArrayList<SSMCache>();
		ssmCaches.add(createCache("UserRepository", cacheClientFactory, addressProvider, cacheConfiguration));
		ssmCaches.add(createCache("SAMLRepository", cacheClientFactory, addressProvider, cacheConfiguration));

		SSMCacheManager ssmCacheManager = new SSMCacheManager();
		ssmCacheManager.setCaches(ssmCaches);
		return ssmCacheManager;

	}

	private SSMCache createCache(String name, MemcacheClientFactoryImpl cacheClientFactory, AddressProvider addressProvider,
			com.google.code.ssm.providers.CacheConfiguration cacheConfiguration) {
		CacheFactory cacheFactory = new CacheFactory();
		cacheFactory.setCacheName(name);
		cacheFactory.setCacheClientFactory(cacheClientFactory);
		cacheFactory.setAddressProvider(addressProvider);
		cacheFactory.setConfiguration(cacheConfiguration);

		Cache object = null;
		try {
			object = cacheFactory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new SSMCache(object, 10000, true);
	}
}

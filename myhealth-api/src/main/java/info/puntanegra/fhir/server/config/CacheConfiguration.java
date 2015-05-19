package info.puntanegra.fhir.server.config;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;
import com.hazelcast.spring.cache.HazelcastCacheManager;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class })
@Profile("!" + Constants.SPRING_PROFILE_CLOUD)
public class CacheConfiguration {

	private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

	@Inject
	private Environment env;

	private CacheManager cacheManager;

	@PreDestroy
	public void destroy() {
		log.info("Closing Cache Manager");
		Hazelcast.shutdownAll();
	}

	@Bean
	public CacheManager cacheManager() {
		log.debug("Hazelcast CacheManager");
		cacheManager = new HazelcastCacheManager(hazelcastInstance());
		return cacheManager;
	}

	private HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		config.setInstanceName("fhirApi");
		config.getNetworkConfig().setPort(5701);
		config.getNetworkConfig().setPortAutoIncrement(true);

		if (env.acceptsProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)) {

			config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
			config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
			config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
		}

		config.getMapConfigs().put("my-sessions", initializeClusteredSession());

		return HazelcastInstanceFactory.newHazelcastInstance(config);
	}

	private MapConfig initializeClusteredSession() {
		MapConfig mapConfig = new MapConfig();

		mapConfig.setBackupCount(env.getProperty("cache.hazelcast.backupCount", Integer.class, 1));
		mapConfig.setTimeToLiveSeconds(env.getProperty("cache.timeToLiveSeconds", Integer.class, 3600));
		return mapConfig;
	}

}

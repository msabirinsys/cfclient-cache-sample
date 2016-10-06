package com.comcast.config;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.spring.cache.CacheBuilder;
import com.couchbase.client.spring.cache.CouchbaseCacheManager;

@Configuration
@EnableCaching
public class CFApplicationConfiguration {
	private static final Logger logger=LoggerFactory.getLogger(CFApplicationConfiguration.class);
	
	@Bean
	DefaultConnectionContext connectionContext(@Value("${cf.apiHost}") String apiHost) {
	    return DefaultConnectionContext.builder()
	        .apiHost(apiHost)
	        .build();
	}
	
	@Bean
	PasswordGrantTokenProvider tokenProvider(@Value("${cf.username}") String username,
	                                         @Value("${cf.password}") String password) {
	    return PasswordGrantTokenProvider.builder()
	        .password(password)
	        .username(username)
	        .build();
	}
	
	@Bean
	ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
	    return ReactorCloudFoundryClient.builder()
	        .connectionContext(connectionContext)
	        .tokenProvider(tokenProvider)
	        .build();
	}

	@Bean
	DefaultCloudFoundryOperations cloudFoundryOperations(CloudFoundryClient cloudFoundryClient,
	                                                     @Value("${cf.organization}") String organization,
	                                                     @Value("${cf.space}") String space) {
	    return DefaultCloudFoundryOperations.builder()
	            .cloudFoundryClient(cloudFoundryClient)
	            .organization(organization)
	            .space(space)
	            .build();
	}
	
	@Bean(destroyMethod = "disconnect")
	public Cluster cluster() {
		return CouchbaseCluster.create();//Arrays.asList(new String[]{"127.0.0.1"})
	}

	@Bean(destroyMethod = "close")
	public Bucket bucket(@Value("${cb.bucket}") String bucket) {
		return cluster().openBucket(bucket, "");
	}

	@Bean
	public CacheManager cacheManager(
			@Value("${cb.cache}") String cache, 
			@Value("${cb.expirationInMillis}") String expirationInMillis, 
			@Autowired Bucket bucket) {
		logger.debug("Time in Millis " + expirationInMillis);
		Map<String, CacheBuilder> mapping = new HashMap<String, CacheBuilder>();
		mapping.put(cache, CacheBuilder.newInstance(bucket).withExpirationInMillis(Integer.parseInt(expirationInMillis)));
		return new CouchbaseCacheManager(mapping);
	}
}
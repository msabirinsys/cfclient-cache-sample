package org.cfapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * This POC has been created to demo the use of 
 * 1. How cloud foundry metadata can be queried like getting applications information
 * 2. How couchbase can be configured to be used as Cache.
 * 
 * This application has an endpoint which allows you to query cloud foundry for the application by name or guuid and saves that information 
 * into cache (couchbase) before returning to the client. Subsequent requests look at the cache not the cloud foundry for the values.
 * Cache can have TTL set, after which value will be deleted and next request will then hit cloud foundry again to pull new value.
 * 
 */
@SpringBootApplication
public class CFApplication {
	private static final Logger logger=LoggerFactory.getLogger(CFApplication.class);
	
	public static void main(String[] args) {
		logger.debug("Starting Application");
		SpringApplication.run(CFApplication.class, args);
	}
}

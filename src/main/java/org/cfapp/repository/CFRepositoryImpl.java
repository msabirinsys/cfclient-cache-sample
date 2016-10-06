package org.cfapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.cfapp.model.CFApplication;
import org.cloudfoundry.operations.CloudFoundryOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/*
 * This repository is used to,
 * 1.	Search cloud foundry metadata for the applications and stores them into cache before returning.
 * 2.	Delete application entry from cache or empty the whole cache.
 */
@Repository
public class CFRepositoryImpl implements CFRepository {
	private static final Logger logger=LoggerFactory.getLogger(CFRepositoryImpl.class);
	private CloudFoundryOperations ops;
	
	public CFRepositoryImpl(@Autowired CloudFoundryOperations ops) {
		this.ops=ops;
	}
	
	/*
	 * This method calls an internal method to grab all applications running in cloud foundry and than filters the 
	 * application using "id" passed as parameter.
	 * @see org.cfapp.repository.CFRepository#getApplicationById(java.lang.String)
	 */
	@Override
	@Cacheable("cfapps-cache")
	public CFApplication getApplicationById(String id) {
		logger.debug("Enter: CFRepositoryImpl.getApplicationById() invoked");
		Optional<CFApplication> app = getAllApplications().stream().filter(a -> a.getId().equals(id)).findFirst();
		return app.isPresent()?app.get():null;
	}

	/*
	 * This method calls an internal method to grab all applications running in cloud foundry and than filters the
	 * application using "name" passed as parameter.
	 * @see org.cfapp.repository.CFRepository#getApplicationByName(java.lang.String)
	 */
	@Override
	public CFApplication getApplicationByName(String name) {
		logger.debug("Enter: CFRepositoryImpl.getApplicationByName() invoked");
		Optional<CFApplication> app=getAllApplications().stream().filter(a -> a.getName().equals(name)).findFirst();
		return app.isPresent()?app.get():null;
	}
	
	/*
	 * A generic method to pull all applications data from cloud foundry.
	 */
	private List<CFApplication> getAllApplications() {
		logger.debug("Enter: getAllApplications()");
		List<CFApplication> apps=
			ops.applications()
			.list()
			.collectList()
			.block()
			.stream()
			.map(app -> new CFApplication(app.getId(), app.getName(), app.getInstances()))
			.collect(Collectors.toList());
		logger.debug("Return: getAllApplications()" + apps);
		return apps;
	}

	/*
	 * This method deletes an entry from Cache (Couchbase)
	 * @see org.cfapp.repository.CFRepository#deleteById(java.lang.String)
	 */
	@Override
	@CacheEvict("cfapps-cache")
	public void deleteById(String id) {
		logger.debug("Deleting cache entry with id " + id);
	}

	/*
	 * This method deletes all entries from Cache (Couchbase)
	 * @see org.cfapp.repository.CFRepository#deleteAll()
	 */
	@Override
	@CacheEvict(cacheNames="cfapps-cache", allEntries=true)
	public void deleteAll() {
		logger.debug("Deleting all cache entries");
	}

}

package org.cfapp.service;

import java.util.Optional;

import org.cfapp.model.CFApplication;
import org.cfapp.repository.CFRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CFApplicationServiceImpl implements CFApplicationService {	
	private static final Logger logger=LoggerFactory.getLogger(CFApplicationServiceImpl.class);
	private CFRepository repository;

	public CFApplicationServiceImpl(@Autowired CFRepository repository) {
		this.repository=repository;
	}

	@Override
	public Optional<CFApplication> getApplicationById(String id) {
		logger.debug("Enter: CFApplicationServiceImpl.getApplicationById() invoked"); 
		return Optional.ofNullable(repository.getApplicationById(id));
	}

	@Override
	public Optional<CFApplication> getApplicationByName(String name) {
		logger.debug("Enter: CFApplicationServiceImpl.getApplicationByName() invoked");
		return Optional.ofNullable(repository.getApplicationByName(name));
	}

	@Override
	public void deleteById(String id) {
		repository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}
}
package org.cfapp.service;

import java.util.Optional;

import org.cfapp.model.CFApplication;

public interface CFApplicationService {
	Optional<CFApplication> getApplicationById(String id);
	Optional<CFApplication> getApplicationByName(String name);
	void deleteById(String id);
	void deleteAll();
}

package org.cfapp.repository;

import org.cfapp.model.CFApplication;

public interface CFRepository {
	CFApplication getApplicationById(String id);
	CFApplication getApplicationByName(String name);
	void deleteById(String id);
	void deleteAll();
}

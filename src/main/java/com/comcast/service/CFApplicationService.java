package com.comcast.service;

import java.util.Optional;

import com.comcast.model.CFApplication;

public interface CFApplicationService {
	Optional<CFApplication> getApplicationById(String id);
	Optional<CFApplication> getApplicationByName(String name);
	void deleteById(String id);
	void deleteAll();
}

package org.cfapp.controller;

import java.util.Optional;

import org.cfapp.model.CFApplication;
import org.cfapp.model.JsonMessage;
import org.cfapp.model.TextMessage;
import org.cfapp.service.CFApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * This controller has somewhat confusing endpoints. Below is what it does.
 * 1.	Searches Cloud Foundry meta-data for applications by id or name. When appId is present as query parameter,it uses it otherwise it uses appName parameter.
 *		If both parameters are blank, it sends an error.
 *		Before returning to the client, results are stored in the cache which is couchbase.
 * 2.	It also allows deletion of the application data from CACHE (couchbase) when request is DELETE. It uses appId query parameter for single entry deletion,
 * 		If no appId found, it deletes all the entries in the cache.
 */
@RestController
public class CFApplicationController {
	private static final Logger logger=LoggerFactory.getLogger(CFApplicationController.class);
	
	private CFApplicationService service;
	public CFApplicationController(@Autowired CFApplicationService service) {
		logger.debug("ApplicationController created ***********************");
		this.service=service;
	}
	
	/*
	 * This endpoint receives GET requests with two optional parameters appId and appName.
	 */
	@GetMapping("/cfapp")
	public ResponseEntity<JsonMessage> getApplication(
			@RequestParam(value="appId", required=false) String appId, 
			@RequestParam(value="appName", required=false) String appName) {
		
		logger.debug("Enter: getAllApps()********");
		if(StringUtils.isEmpty(appId) && StringUtils.isEmpty(appName)) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new TextMessage("Please provide appId or appName as query parameter."));
		}
		
		Optional<CFApplication> app;
		if(!StringUtils.isEmpty(appId)) {
			app=service.getApplicationById(appId);
		} else {
			app=service.getApplicationByName(appName);
		}
		if(app.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(app.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new TextMessage("Application not found"));
	}
	
	/*
	 * This endpoint receives DELETE requests with one optional parameter appId.
	 */
	@DeleteMapping("/cfapp")
	public ResponseEntity<JsonMessage> deleteById(@RequestParam(value="appId", required=false) String appId) {
		logger.debug("Deleting cache for Id " + appId);
		if(StringUtils.isEmpty(appId)) {
			service.deleteAll();
		} else {
			service.deleteById(appId);	
		}
		return ResponseEntity.status(HttpStatus.OK).body(new TextMessage("Entries Deleted Successfully"));
	}
}
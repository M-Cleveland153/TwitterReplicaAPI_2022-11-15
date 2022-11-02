package com.cooksys.assessment_1.services.impl;

import org.springframework.stereotype.Service;

import com.cooksys.assessment_1.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	// GET validate/tag/exists/{label}
	// Checks whether or not a given hashtag exists.
	// Response: 'boolean'

	@Override
	public boolean checkHashtag(String label) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//	GET validate/username/exists/@{username}
	//	Checks whether or not a given username exists.
	//	Response:'boolean'
	@Override
	public boolean checkUsernameExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	//	GET validate/username/available/@{username}
	//	Checks whether or not a given username is available.
	//
	//	Response
	//	'boolean'	
	@Override
	public boolean checkUsernameAvailable(String username) {
		// TODO Auto-generated method stub
		return false;
	}

}

package com.cooksys.assessment_1.services.impl;

import org.springframework.stereotype.Service;

import com.cooksys.assessment_1.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService{

	@Override
	public boolean checkHashtag(String label) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkUsernameExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkUsernameAvailable(String username) {
		// TODO Auto-generated method stub
		return false;
	}

}

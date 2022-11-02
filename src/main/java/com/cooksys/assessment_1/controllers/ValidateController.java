package com.cooksys.assessment_1.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.assessment_1.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/validate")
public class ValidateController {
	
	private final ValidateService validateService;
	
	@GetMapping("/tag/exists/{label}")
	public boolean checkHashtag() {
		return validateService.checkHashtag();
	}
	
	@GetMapping("/username/exists/{username}")
	public boolean checkUsernameExists() {
		return validateService.checkUsernameExists();
	}
	
	@GetMapping("/username/available/{username}")
	public boolean checkUsernameAvailable() {
		return validateService.checkUsernameAvailable();
	}
}

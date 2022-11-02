package com.cooksys.assessment_1.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.assessment_1.dtos.CredentialsDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.dtos.UserRequestDto;
import com.cooksys.assessment_1.dtos.UserResponseDto;
import com.cooksys.assessment_1.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

	private final UserService userService;
	
	@GetMapping
	public List<UserResponseDto> getAllUsers(){
		return userService.getAllUsers();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponseDto createUser(@RequestBody UserRequestDto userRequestDto) {
		return userService.createUser(userRequestDto);
	}
	
	@DeleteMapping("/{username}")
	public UserResponseDto deleteUser(@RequestBody CredentialsDto credentialsDto) {
		return userService.deleteUser(credentialsDto);
	}
	
//	@PostMapping("/{username}/follow")
//	public void followUser(@RequestBody CredentialsDto credentialsDto) {
//		return userService.followUser(credentialsDto);
//	}
	
//	@PostMapping("/{username}/unfollow")
//	public void unfollowUser(@RequestBody CredentialsDto credentialsDto) {
//		return userService.unfollowUser(credentialsDto);
//	}
	
	@GetMapping("/{username}/feed") 
	public List<TweetResponseDto> getAllUserTweets(){
		return userService.getAllUserTweets();
	}
	
	@GetMapping("/{username}/mentions")
	public List<TweetResponseDto> getAllUserMentions(){
		return userService.getAllUserMentions();
	}
	
	@GetMapping("/{username}/followers")
	public List<UserResponseDto> getAllUserFollowers(){
		return userService.getAllUserFollowers();
	}
	
	@GetMapping("/{username}/following") 
	public List<UserResponseDto> getAllUsersFollowed(){
		return userService.getAllUsersFollowed();
	}
}
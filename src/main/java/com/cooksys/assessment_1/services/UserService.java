package com.cooksys.assessment_1.services;

import java.util.List;

import com.cooksys.assessment_1.dtos.CredentialsDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.dtos.UserRequestDto;
import com.cooksys.assessment_1.dtos.UserResponseDto;

public interface UserService {

	List<UserResponseDto> getAllUsers();

	UserResponseDto createUser(UserRequestDto userRequestDto);

	UserResponseDto deleteUser(CredentialsDto credentialsDto);

	//	Object followUser(CredentialsDto credentialsDto);

	//	Object unfollowUser(CredentialsDto credentialsDto);

	List<TweetResponseDto> getAllUserTweets();

	List<TweetResponseDto> getAllUserMentions();

	List<UserResponseDto> getAllUserFollowers();

	List<UserResponseDto> getAllUsersFollowed();


	
}

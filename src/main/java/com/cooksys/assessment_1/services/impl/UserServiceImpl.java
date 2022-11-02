package com.cooksys.assessment_1.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.assessment_1.dtos.CredentialsDto;
import com.cooksys.assessment_1.dtos.ProfileDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.dtos.UserRequestDto;
import com.cooksys.assessment_1.dtos.UserResponseDto;
import com.cooksys.assessment_1.entities.Credentials;
import com.cooksys.assessment_1.entities.Profile;
import com.cooksys.assessment_1.entities.User;
import com.cooksys.assessment_1.exceptions.NotFoundException;
import com.cooksys.assessment_1.mappers.CredentialsMapper;
import com.cooksys.assessment_1.mappers.ProfileMapper;
import com.cooksys.assessment_1.mappers.UserMapper;
import com.cooksys.assessment_1.repositories.UserRepository;
import com.cooksys.assessment_1.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	
	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final CredentialsMapper credentialsMapper;
	private final ProfileMapper profileMapper;
	
	
	public User getUserByUsername(String username) {
		List<User> users = userRepository.findAll();
		Optional<User> optionalUser;
		
		for(User user: users) {
			if(user.getCredentials().getUsername().equals(username)) {
				Long id = user.getId();
				optionalUser = userRepository.findByIdAndDeletedFalse(id);
				return optionalUser.get();
			}
		}
		throw new NotFoundException("User with username: " + username + " not found");
	}
	
	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.findAll());
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		User userToCreate = userMapper.requestDtoToEntity(userRequestDto);
		
		CredentialsDto userCredentialsDto = userRequestDto.getCredentials();
		Credentials userCredentials = credentialsMapper.dtoToEntity(userCredentialsDto);
		
		ProfileDto userProfileDto = userRequestDto.getProfile();
		Profile userProfile = profileMapper.requestDtoToEntity(userProfileDto);
		
		userToCreate.setCredentials(userCredentials);
		userToCreate.setProfile(userProfile);
		
		return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));
	}
	
	@Override
	public UserResponseDto getUser(String username) {
		
		return userMapper.entityToDto(userRepository.saveAndFlush(getUserByUsername(username)));
	}
	
	@Override
	public UserResponseDto updateUser(String username, CredentialsDto credentialsDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentialsDto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TweetResponseDto> getAllUserTweets(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TweetResponseDto> getAllUserMentions(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserResponseDto> getAllUserFollowers(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserResponseDto> getAllUsersFollowed(String username) {
		// TODO Auto-generated method stub
		return null;
	}


}

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
import com.cooksys.assessment_1.exceptions.BadRequestException;
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
	
	
	// Create CheckCredentials/CheckProfile methods takes in credentials/profile
	
	public User getUserByUsername(String username) {
		List<User> users = userRepository.findAll();
		Optional<User> optionalUser;
		
		for(User user: users) {
			if(user.getCredentials().getUsername().equals(username)) {
				// Checks if user is deleted
				if(user.isDeleted()) {
					throw new BadRequestException("This user has been deleted");
				}
				Long id = user.getId();
				optionalUser = userRepository.findByIdAndDeletedFalse(id);
				return optionalUser.get();
			}
		}
		// At this point user does not exist
		throw new NotFoundException("User with username: " + username + " not found");
	}
	
	@Override
	public List<UserResponseDto> getAllUsers() {
		return userMapper.entitiesToDtos(userRepository.findAllByDeletedFalse());
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		User userToCreate = userMapper.requestDtoToEntity(userRequestDto);
		List<User> users = userRepository.findAll();
		
		CredentialsDto userCredentialsDto = userRequestDto.getCredentials();
		Credentials userCredentials = credentialsMapper.dtoToEntity(userCredentialsDto);
		
		// Checks if username or password is missing
		if(userCredentials.getPassword() == null || userCredentials.getUsername() == null) {
			throw new BadRequestException("Missing username or password");
		}
		
		// Checks if username given is already taken
		for(User user : users) {
			if(user.getCredentials().getUsername().equals(userCredentials.getUsername())) {
				throw new BadRequestException("This username already exists");
			}
		}
		
		ProfileDto userProfileDto = userRequestDto.getProfile();
		Profile userProfile = profileMapper.requestDtoToEntity(userProfileDto);
		
		// Checks if email is missing
		if(userProfile.getEmail() == null) {
			throw new BadRequestException("Email is required");
		}
		
		userToCreate.setCredentials(userCredentials);
		userToCreate.setProfile(userProfile);
		
		return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));
	
	}
	
	@Override
	public UserResponseDto getUser(String username) {
		return userMapper.entityToDto(userRepository.saveAndFlush(getUserByUsername(username)));
	}
	
	@Override
	public UserResponseDto updateUser(String username, CredentialsDto credentialsDto, ProfileDto profileDto) {
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

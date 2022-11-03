package com.cooksys.assessment_1.services.impl;

import java.util.ArrayList;
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
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final UserRepository userRepository;
	private final CredentialsMapper credentialsMapper;
	private final ProfileMapper profileMapper;

	// Helper methods

	// Checkers when a UserRequestDto is requested
	// Checks Credentials of UserRequestDto whether they are missing required fields
	// or if already exists
	public Credentials checkCredentials(Credentials credentials) {
		List<User> users = userRepository.findAll();
		if (credentials.getPassword() == null || credentials.getUsername() == null) {
			throw new BadRequestException("Missing username or password");
		}
		for (User user : users) {
			if (user.getCredentials().getUsername().equals(credentials.getUsername())) {
				throw new BadRequestException("This username already exists");
			}
		}

		return credentials;
	}

	// Checks Profile of UserRequestDto if missing required field
	public Profile checkProfile(Profile profile) {
		if (profile.getEmail() == null) {
			throw new BadRequestException("Email is required");
		}
		return profile;
	}

	// Checks if User has been deleted or Does Not Exist then returns correct User
	// if only username/CredentialsDto is requested
	public User getUserByCredentials(Credentials credentials) {
		List<User> users = userRepository.findAll();
		Optional<User> optionalUser;

		for (User user : users) {
			if (user.getCredentials().equals(credentials)) {
				if (user.isDeleted()) {
					throw new BadRequestException("This user has been deleted");
				}
				Long id = user.getId();
				optionalUser = userRepository.findByIdAndDeletedFalse(id);
				return optionalUser.get();
			}
		}
		throw new NotFoundException(
				"User with username: " + credentials.getUsername() + " and password " + credentials.getPassword());
	}

	// Checks if User has been deleted or Does Not Exist then returns correct User
	// if only username/CredentialsDto is requested
	public User getUserByUsername(String username) {
		List<User> users = userRepository.findAll();
		Optional<User> optionalUser;

		for (User user : users) {
			if (user.getCredentials().getUsername().equals(username)) {
				// Checks if user is deleted
				if (user.isDeleted()) {
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

	// ENDPOINTS

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

		for (User user : users) {
			if (user.getCredentials().equals(userCredentials) && user.isDeleted() == true) {
				user.setDeleted(false);
				userToCreate = user;
				return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));
			} else if (user.getCredentials().getUsername().equals(userCredentials.getUsername())) {
				throw new BadRequestException("Username already exists");
			}
		}

		ProfileDto userProfileDto = userRequestDto.getProfile();
		Profile userProfile = profileMapper.dtoToEntity(userProfileDto);

		checkCredentials(userCredentials);
		checkProfile(userProfile);

		userToCreate.setCredentials(userCredentials);
		userToCreate.setProfile(userProfile);

		return userMapper.entityToDto(userRepository.saveAndFlush(userToCreate));

	}

	@Override
	public UserResponseDto getUser(String username) {
		return userMapper.entityToDto(userRepository.saveAndFlush(getUserByUsername(username)));
	}

	@Override
	public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
		User userToUpdate = getUserByUsername(username);
		Credentials credentials = credentialsMapper.dtoToEntity(userRequestDto.getCredentials());

		if (!userToUpdate.getCredentials().equals(credentials)) {
			throw new BadRequestException("Given credentials don't match designated user's credentials");
		}

		Profile newProfile = profileMapper.dtoToEntity(userRequestDto.getProfile());
		userToUpdate.setProfile(newProfile);
		return userMapper.entityToDto(userRepository.saveAndFlush(userToUpdate));
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentialsDto) {
		User userToDelete = getUserByUsername(username);
		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);

		if (!userToDelete.getCredentials().equals(credentials)) {
			throw new BadRequestException("Given credentials don't match designated user's credentials");
		}

		userToDelete.setDeleted(true);
		return userMapper.entityToDto(userRepository.saveAndFlush(userToDelete));

	}

	@Override
	public void followUser(String username, CredentialsDto credentialsDto) {
		User followedUser = getUserByUsername(username);
		List<User> userFollowers = followedUser.getFollowers();

		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);
		User followingUser = getUserByCredentials(credentials);

		for (User user : userFollowers) {
			if (user.equals(followingUser)) {
				throw new BadRequestException("User is already following " + username);
			}
		}

		followedUser.getFollowers().add(followingUser);
		userRepository.saveAndFlush(followedUser);

	}

	@Override
	public void unfollowUser(String username, CredentialsDto credentialsDto) {
		User followedUser = getUserByUsername(username);
		List<User> userFollowers = followedUser.getFollowers();

		Credentials credentials = credentialsMapper.dtoToEntity(credentialsDto);
		User unfollowingUser = getUserByCredentials(credentials);

		if (!userFollowers.contains(unfollowingUser))
			throw new BadRequestException("User was not following: " + username);

		followedUser.getFollowers().remove(unfollowingUser);
		userRepository.saveAndFlush(followedUser);

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
		User userWithFollowers = getUserByUsername(username);
		List<User> userFollowers = userWithFollowers.getFollowers();
		List<User> newUserFollowers = new ArrayList<>();
		
		for(User user : userFollowers) {
			if(!user.isDeleted()) {
				newUserFollowers.add(user);
			}
		}
		
		return userMapper.entitiesToDtos(userRepository.saveAllAndFlush(newUserFollowers));
	}

	@Override
	public List<UserResponseDto> getAllUsersFollowed(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}

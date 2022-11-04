package com.cooksys.assessment_1.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cooksys.assessment_1.dtos.ContextDto;
import com.cooksys.assessment_1.dtos.CredentialsDto;
import com.cooksys.assessment_1.dtos.HashtagResponseDto;
import com.cooksys.assessment_1.dtos.TweetRequestDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.dtos.UserResponseDto;

import com.cooksys.assessment_1.entities.Credentials;
import com.cooksys.assessment_1.entities.Hashtag;
import com.cooksys.assessment_1.entities.Tweet;
import com.cooksys.assessment_1.entities.User;
import com.cooksys.assessment_1.exceptions.BadRequestException;
import com.cooksys.assessment_1.exceptions.NotAuthorizedException;
import com.cooksys.assessment_1.exceptions.NotFoundException;
import com.cooksys.assessment_1.mappers.CredentialsMapper;
import com.cooksys.assessment_1.mappers.HashtagMapper;

import com.cooksys.assessment_1.mappers.TweetMapper;
import com.cooksys.assessment_1.mappers.UserMapper;
import com.cooksys.assessment_1.repositories.HashtagRepository;
import com.cooksys.assessment_1.repositories.TweetRepository;
import com.cooksys.assessment_1.repositories.UserRepository;
import com.cooksys.assessment_1.services.TweetService;

import ch.qos.logback.core.joran.conditional.IfAction;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetMapper tweetMapper;
	private final UserMapper userMapper;
	private final CredentialsMapper credentialsMapper;
	private final HashtagMapper hashtagMapper;

	private final TweetRepository tweetRepository;
	private final UserRepository userRepository;
	private final HashtagRepository hashtagRepository;

	// ------------------------------------
	// --------- Helper Methods -----------
	// ------------------------------------

	private Tweet getTweet(Long id) {
		Optional<Tweet> optionalTweet = tweetRepository.findById(id);
		if (optionalTweet.isEmpty()) {
			throw new NotFoundException("Tweet of ID: " + id + " not found.");
		}
		return optionalTweet.get();
	}

	private List<Tweet> removeDeletedTweets(List<Tweet> tweets) {
		List<Tweet> tweetsWithNoDeletes = new ArrayList<>();
		for (Tweet tweet : tweets) {
			if (tweet.isDeleted() == false) {
				tweetsWithNoDeletes.add(tweet);
			}
		}
		return tweetsWithNoDeletes;
	}

	// Method that parses tweet content and handles Hashtags and User Mentions
	private Tweet parseHashtagsAndMentions(Tweet createdTweet) {
		if (createdTweet.getContent().contains("#")) {
			// Use regex to isolate hashtags
			String patternStr = "(#+[a-zA-Z0-9(_)]{1,})";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(createdTweet.getContent());

			// create List<String> to populate with hashtags found
			List<String> hashtagsInContent = new ArrayList<>();
			while (matcher.find()) {
				hashtagsInContent.add(matcher.group());
			}

			// Check hashtags against the hashtagRepo, and add if they don't exist
			for (String hashtag : hashtagsInContent) {
				Hashtag newHashtag = hashtagRepository.findByLabel(hashtag);

				if (newHashtag == null) {
					newHashtag = new Hashtag();
					newHashtag.setLabel(hashtag);
					hashtagRepository.saveAndFlush(newHashtag);
				}

				// Create list for Tweet hashtags if null
				// There might be a way to do this with a Try/Catch?
				if (createdTweet.getHashtags() == null) {
					createdTweet.setHashtags(new ArrayList<Hashtag>());
				}

				List<Hashtag> existingHashtags = createdTweet.getHashtags();
				existingHashtags.add(newHashtag);
				createdTweet.setHashtags(existingHashtags);
			}
		}

		// Check for user mentions in the content
		if (createdTweet.getContent().contains("@")) {
			// Use regex to isolate user mentions
			String patternStr = "(@+[a-zA-Z0-9(_)]{1,})";
			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(createdTweet.getContent());

			// create List<String> to populate with mentioned users found
			List<String> userMentionsInContent = new ArrayList<>();
			while (matcher.find()) {
				userMentionsInContent.add(matcher.group().replace("@", ""));
			}

			for (String username : userMentionsInContent) {
				// Check username against the userRepo to see if it exists, grab if so
				User userFromRepo = userRepository.findByCredentialsUsername(username);

				if (userFromRepo != null && userFromRepo.isDeleted() == false) {
					if (createdTweet.getMentionedUsers() == null) {
						// Create list for user mentions
						createdTweet.setMentionedUsers(new ArrayList<User>());
					}

					// Add user from repo to list of mentioned users
					List<User> existingMentions = createdTweet.getMentionedUsers();
					existingMentions.add(userFromRepo);
					createdTweet.setMentionedUsers(existingMentions);
				}
			}
		}

		return createdTweet;
	}

	// ------------------------------------
	// ----------- End Points -------------
	// ------------------------------------

	@Override
	public List<TweetResponseDto> getAllTweets() {
		// Retrieve all tweets from the repo, and remove deleted tweets
		List<Tweet> allTweets = removeDeletedTweets(tweetRepository.findAll());

		// Sort remaining tweets in reverse chronological order
		allTweets.sort((e1, e2) -> e2.getPosted().compareTo(e1.getPosted()));
		return tweetMapper.entitiesToDtos(allTweets);
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
		// Check credentials against the repo and grab user if it exists
		Credentials incomingCredentials = credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials());
		User user = userRepository.findByCredentials(incomingCredentials);
		if (user == null)
			throw new NotFoundException("Incorrect username or password.");

		// Create base tweet
		Tweet createdTweet = new Tweet();
		createdTweet.setAuthor(user);
		createdTweet.setContent(tweetRequestDto.getContent());
		parseHashtagsAndMentions(createdTweet);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(createdTweet));
	}

	@Override
	public TweetResponseDto getTweetById(Long id) {
		return tweetMapper.entityToDto(getTweet(id));
	}

	@Override
	public TweetResponseDto deleteTweetById(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = getTweet(id);
		Credentials incomingCredentials = credentialsMapper.dtoToEntity(credentialsDto);

		if (!tweet.getAuthor().getCredentials().equals(incomingCredentials))
			throw new NotAuthorizedException("Invalid login information to make this request.");

		tweet.setDeleted(true);
		return tweetMapper.entityToDto(tweet);
	}

	@Override
	public void likeTweet(Long id, CredentialsDto credentialsDto) {
		Tweet tweet = getTweet(id);
		User user = userRepository.findByCredentials(credentialsMapper.dtoToEntity(credentialsDto));

		if (user == null)
			throw new NotAuthorizedException("Invalid login information to make this request.");

		List<Tweet> existingLikedTweets = user.getLikedTweets();
		if (existingLikedTweets == null)
			existingLikedTweets = new ArrayList<Tweet>();
		existingLikedTweets.add(tweet);

		userRepository.saveAndFlush(user);
	}

	@Override
	public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto) {
		Tweet targetTweet = getTweet(id);
		Tweet replyTweet = tweetMapper.DtoToEntity(tweetRequestDto);
		User user = userRepository.findByCredentials(credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials()));

		if (user == null)
			throw new NotAuthorizedException("Invalid login information to make this request.");
		if (targetTweet.isDeleted())
			throw new NotFoundException("Tweet of ID: " + id + " is no longer available.");
		if (replyTweet.getContent() == null)
			throw new BadRequestException("Tweet must contain content.");

		replyTweet.setAuthor(user);
		replyTweet.setInReplyTo(targetTweet);
		parseHashtagsAndMentions(replyTweet);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(replyTweet));
	}

	@Override
	public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
		Tweet targetTweet = getTweet(id);
		User user = userRepository.findByCredentials(credentialsMapper.dtoToEntity(credentialsDto));

		if (user == null)
			throw new NotAuthorizedException("Invalid login information to make this request.");
		if (targetTweet.isDeleted())
			throw new NotFoundException("Tweet of ID: " + id + " is no longer available.");

		Tweet repostTweet = new Tweet();
		repostTweet.setAuthor(user);
		repostTweet.setRepostOf(targetTweet);
		targetTweet.getReposts().add(repostTweet);
		tweetRepository.saveAndFlush(targetTweet);

		return tweetMapper.entityToDto(tweetRepository.saveAndFlush(repostTweet));
	}

	@Override
	public List<HashtagResponseDto> getAllHashtagsByTweetId(Long id) {
		return hashtagMapper.entitiesToDtos(getTweet(id).getHashtags());
	}

	@Override
	public List<UserResponseDto> getAllLikesByTweetId(Long id) {
		List<User> userLikes = new ArrayList<>();
		for (User user : getTweet(id).getLikedByUsers()) {
			if (user.isDeleted() == false) {
				userLikes.add(user);
			}
		}
		return userMapper.entitiesToDtos(userLikes);
	}

//		CONTEXT    
//    The reply context of a tweet. The before property represents the chain of replies that led to the target tweet, 
//    and the after property represents the chain of replies that followed the target tweet.
//	  The chains should be in chronological order, and the after chain should include all replies of replies, 
//    meaning that all branches of replies must be flattened into a single chronological list to fully satisfy the requirements.

	// ToDo: Retrieves the context of the tweet with the given id. If that tweet is
	// deleted or otherwise doesn’t exist,
	// an error should be sent in lieu of a response.
	// IMPORTANT: While deleted tweets should not be included in the before and
	// after properties of the result,
	// transitive replies should. What that means is that if a reply to the target
	// of the context is deleted, but there’s
	// another reply to the deleted reply, the deleted reply should be excluded but
	// the other reply should remain.
	// RESPONSE: 'Context'

	// STATUS: COMPLETE
	// [IMPLEMENTED] If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.
	// [IMPLEMENTED] Excluded deleted tweets 
	// [IMPLEMENTED] Include transitive tweets
	@Override
	public ContextDto getContextByTweetId(Long id) {

		Optional<Tweet> optionalTargetTweet = tweetRepository.findByIdAndDeletedFalse(id);
		if(optionalTargetTweet.isEmpty()) {
			throw new NotFoundException("No tweet found with id: " + id);
		}
		Tweet targetTweetEntity = optionalTargetTweet.get();

		TweetResponseDto targetTweetResponseDto = tweetMapper.entityToDto(optionalTargetTweet.get());
		ContextDto contextDto = new ContextDto();
		contextDto.setTarget(targetTweetResponseDto);

		// get "before" tweets
		List<Tweet> beforeTweetEntities = new ArrayList<>();
		Tweet newTargetTweetEntity = targetTweetEntity;
		while (newTargetTweetEntity.getInReplyTo() != null) {
			newTargetTweetEntity = newTargetTweetEntity.getInReplyTo();
			beforeTweetEntities.add(newTargetTweetEntity);
		}
		Collections.sort(beforeTweetEntities, Comparator.comparing(Tweet::getPosted));
		beforeTweetEntities = removeDeletedTweets(beforeTweetEntities);		
		
		List<TweetResponseDto> beforeTweetResponseDtos = tweetMapper.entitiesToDtos(beforeTweetEntities);
		contextDto.setBefore(beforeTweetResponseDtos);

		// get "after" tweets
		List<Tweet> directRepliesTweets = targetTweetEntity.getReplies();
		Set<Tweet> replySet1 = new HashSet<>(directRepliesTweets);
		Set<Tweet> replySet2 = new HashSet<>();
		while (replySet1 != replySet2) {
			replySet2 = replySet1;
			for (Tweet reply : replySet1) {
				replySet1.addAll(reply.getReplies());
			}
		}
		List<Tweet> afterTweetEntities = new ArrayList<>(replySet1);
		afterTweetEntities = removeDeletedTweets(afterTweetEntities);
		Collections.sort(afterTweetEntities, Comparator.comparing(Tweet::getPosted));
		List<TweetResponseDto> afterTweetResponseDtoList = tweetMapper.entitiesToDtos(afterTweetEntities);
		contextDto.setAfter(afterTweetResponseDtoList);

		return contextDto;

	}

	@Override
	public List<TweetResponseDto> getRepliesByTweetId(Long id) {
		Tweet targetTweet = getTweet(id);
		List<Tweet> tweetReplies = new ArrayList<>();
		for (Tweet tweet : targetTweet.getReplies()) {
			if (tweet.isDeleted())
				continue;
			tweetReplies.add(tweet);
		}

		return tweetMapper.entitiesToDtos(tweetReplies);
	}

	@Override
	public List<TweetResponseDto> getRepostsByTweetId(Long id) {
		Tweet targetTweet = getTweet(id);
		List<Tweet> tweetReposts = new ArrayList<>();
		for (Tweet tweet : targetTweet.getReposts()) {
			if (tweet.isDeleted())
				continue;
			tweetReposts.add(tweet);
		}

		return tweetMapper.entitiesToDtos(tweetReposts);
	}

	@Override
	public List<UserResponseDto> getMentionsByTweetId(Long id) {
		Tweet targetTweet = getTweet(id);
		List<User> userMentions = new ArrayList<>();
		for (User user : targetTweet.getMentionedUsers()) {
			if (user.isDeleted())
				continue;
			userMentions.add(user);
		}

		return userMapper.entitiesToDtos(userMentions);
	}

}

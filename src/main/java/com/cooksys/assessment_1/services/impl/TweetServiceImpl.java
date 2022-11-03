package com.cooksys.assessment_1.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        for (Tweet tweet : tweets)
        {
            if (tweet.isDeleted() == false)
            {
                tweetsWithNoDeletes.add(tweet);
            }
        }
        return tweetsWithNoDeletes;
    }

    // Method that parses tweet content and handles Hashtags and User Mentions
    private Tweet parseHashtagsAndMentions(Tweet createdTweet) {
        if (createdTweet.getContent().contains("#"))
        {
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
            for (String hashtag : hashtagsInContent)
            {
                Hashtag newHashtag = hashtagRepository.findByLabel(hashtag);

                if (newHashtag == null)
                {
                    newHashtag = new Hashtag();
                    newHashtag.setLabel(hashtag);
                    hashtagRepository.saveAndFlush(newHashtag);
                }

                // Create list for Tweet hashtags if null
                // There might be a way to do this with a Try/Catch?
                if (createdTweet.getHashtags() == null)
                {
                    createdTweet.setHashtags(new ArrayList<Hashtag>());
                }

                List<Hashtag> existingHashtags = createdTweet.getHashtags();
                existingHashtags.add(newHashtag);
                createdTweet.setHashtags(existingHashtags);
            }
        }

        // Check for user mentions in the content
        if (createdTweet.getContent().contains("@"))
        {
            // Use regex to isolate user mentions
            String patternStr = "(@+[a-zA-Z0-9(_)]{1,})";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(createdTweet.getContent());

            // create List<String> to populate with mentioned users found
            List<String> userMentionsInContent = new ArrayList<>();
            while (matcher.find()) {
                userMentionsInContent.add(matcher.group().replace("@", ""));
            }

            for (String username : userMentionsInContent)
            {
                // Check username against the userRepo to see if it exists, grab if so
                User userFromRepo = userRepository.findByCredentialsUsername(username);

                if (userFromRepo != null && userFromRepo.isDeleted() == false)
                {
                    if (createdTweet.getMentionedUsers() == null)
                    {
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
        if (user == null) throw new NotFoundException("Incorrect username or password.");

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
        if (existingLikedTweets == null) existingLikedTweets = new ArrayList<Tweet>();
        existingLikedTweets.add(tweet);

        userRepository.saveAndFlush(user);
    }
        
    @Override
    public TweetResponseDto replyToTweet(Long id, TweetRequestDto tweetRequestDto) {
        Tweet targetTweet = getTweet(id);
        Tweet replyTweet = tweetMapper.DtoToEntity(tweetRequestDto);
        User user = userRepository.findByCredentials(credentialsMapper.dtoToEntity(tweetRequestDto.getCredentials()));
        
        if (user == null) throw new NotAuthorizedException("Invalid login information to make this request.");
        if (targetTweet.isDeleted()) throw new NotFoundException("Tweet of ID: " + id + " is no longer available.");
        if (replyTweet.getContent() == null) throw new BadRequestException("Tweet must contain content.");

        replyTweet.setAuthor(user);
        replyTweet.setInReplyTo(targetTweet);
        parseHashtagsAndMentions(replyTweet);

        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(replyTweet));
    }
        
    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
        Tweet targetTweet = getTweet(id);
        User user = userRepository.findByCredentials(credentialsMapper.dtoToEntity(credentialsDto));
        
        if (user == null) throw new NotAuthorizedException("Invalid login information to make this request.");
        if (targetTweet.isDeleted()) throw new NotFoundException("Tweet of ID: " + id + " is no longer available.");
        
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
        // Deleted users should be excluded from the response.
        List<User> userLikes = new ArrayList<>();
        for (User user : getTweet(id).getLikedByUsers())
        {
            if (user.isDeleted() == false)
            {
                userLikes.add(user);
            }
        }
        return userMapper.entitiesToDtos(userLikes);
    }

    @Override
    public ContextDto getContextByTweetId(Long id) {
        // 	ToDo: Retrieves the context of the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, 
    	//	an error should be sent in lieu of a response.
        // 	IMPORTANT: While deleted tweets should not be included in the before and after properties of the result, 
    	//	transitive replies should. What that means is that if a reply to the target of the context is deleted, but there’s 
    	//	another reply to the deleted reply, the deleted reply should be excluded but the other reply should remain.
        return null;
    }
    
    
    // NOT YET TESTED IN POSTMAN
    @Override
    public List<TweetResponseDto> getRepliesByTweetId(Long id) {
        // ToDo: Retrieves the direct replies to the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, 
    	// an error should be sent in lieu of a response.
        // Deleted replies to the tweet should be excluded from the response.
    	
    	List<Tweet> tweets = tweetRepository.findAll();
    	List<Tweet> replies = new ArrayList<>(); 
    	for(Tweet tweet: tweets) {
    		if(tweet.getInReplyTo().getId() == id) {
    			replies.add(tweet);    			
    		}
    	}
    	return tweetMapper.entitiesToDtos(replies);
    }

    // NOT YET TESTED IN POSTMAN
    @Override
    public List<TweetResponseDto> getRepostsByTweetId(Long id) {
        // ToDo: Retrieves the direct reposts of the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, 
    	// an error should be sent in lieu of a response.
        // Deleted reposts of the tweet should be excluded from the response.
    	
    	List<Tweet> tweets = tweetRepository.findAll();
    	List<Tweet> reposts = new ArrayList<>(); 
    	for(Tweet tweet: tweets) {
    		if(tweet.getRepostOf().getId() == id) {
    			reposts.add(tweet);    			
    		}
    	}
    	return tweetMapper.entitiesToDtos(reposts);
    }
    
    // IN PROGRESS
    @Override
    public List<UserResponseDto> getMentionsByTweetId(Long id) {
        // ToDo: Retrieves the users mentioned in the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, 
    	// an error should be sent in lieu of a response.
        // Deleted users should be excluded from the response.
        // IMPORTANT Remember that tags and mentions must be parsed by the server!
    	
    	Optional<Tweet> optionalTweet = tweetRepository.findById(id);
    	Tweet tweet = optionalTweet.get();
    	
    	//List<TweetResponseDto> userMentionsList = tweet
    	
    	
        return null;
    }
    
}
            
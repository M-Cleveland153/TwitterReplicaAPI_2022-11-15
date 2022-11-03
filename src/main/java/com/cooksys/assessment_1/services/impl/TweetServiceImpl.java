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

        // Check for Hashtags in the content
        if (createdTweet.getContent().contains("#"))
        {
            // Use regex to isolate hashtags
            String patternStr = "(?:\\|A)[##]+([A-Za-z0-9-_]+)";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(createdTweet.getContent());

            // create List<String> to populate with hashtags found
            List<String> hashtagsInContent = new ArrayList<>();
            while (matcher.find()) {
                hashtagsInContent.add(matcher.group().replace("#", ""));
            }

            for (String hashtag : hashtagsInContent)
            {
                // Check hashtags against the hashtagRepo, and add if they don't exist
                Hashtag newHashtag = hashtagRepository.findByLabel(hashtag);

                if (newHashtag == null)
                {
                    newHashtag = new Hashtag();
                    newHashtag.setLabel(hashtag);
                    hashtagRepository.saveAndFlush(newHashtag);
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
            String patternStr = "(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)";
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

                if (userFromRepo != null)
                {
                    List<User> existingMentions = createdTweet.getMentionedUsers();
                    existingMentions.add(userFromRepo);
                    createdTweet.setMentionedUsers(existingMentions);
                }
            }
        }
        
        return tweetMapper.entityToDto(tweetRepository.saveAndFlush(createdTweet));
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        return tweetMapper.entityToDto(getTweet(id));
    }

    @Override
    public TweetResponseDto deleteTweetById(Long id, CredentialsDto credentialsDto) {
        // ToDo: “Deletes” the tweet with the given id. If no such tweet exists or the provided credentials do not match author of the tweet, an error should be sent in lieu of a response. If a tweet is successfully “deleted”, the response should contain the tweet data prior to deletion.

        // IMPORTANT: This action should not actually drop any records from the database! Instead, develop a way to keep track of “deleted” tweets so that even if a tweet is deleted, data with relationships to it (like replies and reposts) are still intact.
        return null;
    }
    
    @Override
    public void likeTweet(Long id, CredentialsDto credentialsDto) {
        // ToDo: Creates a “like” relationship between the tweet with the given id and the user whose credentials are provided by the request body. If the tweet is deleted or otherwise doesn’t exist, or if the given credentials do not match an active user in the database, an error should be sent. Following successful completion of the operation, no response body is sent.
    
    }
        
    @Override
    public TweetResponseDto replyToTweet(Long id, CredentialsDto credentialsDto) {
        // ToDo: Creates a reply tweet to the tweet with the given id. The author of the newly-created tweet should match the credentials provided by the requestbody. If the given tweet is deleted or otherwise doesn’t exist, or if the given credentials do not match an active user in the database, an error should be sent in lieu of a response.
    
        // Because this creates a reply tweet, content is not optional. Additionally, notice that the inReplyTo property is not provided by the request. The server must create that relationship.
    
        // The response should contain the newly-created tweet.
    
        // IMPORTANT: when a tweet with content is created, the server must process the tweet’s content for @{username} mentions and #{hashtag} tags. There is no way to create hashtags or create mentions from the API, so this must be handled automatically!
        return null;
    }
        
    @Override
    public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
        // ToDo: Creates a repost of the tweet with the given id. The author of the repost should match the credentials provided in the request body. If the given tweet is deleted or otherwise doesn’t exist, or the given credentials do not match an active user in the database, an error should be sent in lieu of a response.
    
        // Because this creates a repost tweet, content is not allowed. Additionally, notice that the repostOf property is not provided by the request. The server must create that relationship.
    
        // The response should contain the newly-created tweet.
        return null;
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

    @Override
    public List<UserResponseDto> getMentionsByTweetId(Long id) {
        // ToDo: Retrieves the users mentioned in the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, 
    	// an error should be sent in lieu of a response.
        // Deleted users should be excluded from the response.

        // IMPORTANT Remember that tags and mentions must be parsed by the server!
        return null;
    }
    
}
            
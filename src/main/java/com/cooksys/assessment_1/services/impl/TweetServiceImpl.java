package com.cooksys.assessment_1.services.impl;

import java.util.List;

import com.cooksys.assessment_1.dtos.ContextDto;
import com.cooksys.assessment_1.dtos.TweetRequestDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.services.TweetService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    @Override
    public List<TweetResponseDto> getAllTweets() {
        // ToDo: Retrieves all (non-deleted) tweets. The tweets should appear in reverse-chronological order.
        return null;
    }

    @Override
    public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {
        // ToDo: Because this always creates a simple tweet, it must have a content property and may not have inReplyTo or repostOf properties.

        // Creates a new simple tweet, with the author set to the user identified by the credentials in the request body. If the given credentials do not match an active user in the database, an error should be sent in lieu of a response.

        // IMPORTANT: when a tweet with content is created, the server must process the tweet’s content for @{username} mentions and #{hashtag} tags. There is no way to create hashtags or create mentions from the API, so this must be handled automatically!
        return null;
    }

    @Override
    public TweetResponseDto getTweetById(Long id) {
        // ToDo: Retrieves a tweet with a given id. If no such tweet exists, or the given tweet is deleted, an error should be sent in lieu of a response.
        return null;
    }

    // @Override
    // public TweetResponseDto deleteTweetById(Long id, CredentialsDto credentialsDto) {
    //     // ToDo: “Deletes” the tweet with the given id. If no such tweet exists or the provided credentials do not match author of the tweet, an error should be sent in lieu of a response. If a tweet is successfully “deleted”, the response should contain the tweet data prior to deletion.

    //     // IMPORTANT: This action should not actually drop any records from the database! Instead, develop a way to keep track of “deleted” tweets so that even if a tweet is deleted, data with relationships to it (like replies and reposts) are still intact.
    //     return null;
    // }

    
    
    // ----------------------------------------------------------------------------------------------
    // --------------- Below methods commented out due to pending access to DTOs --------------------
    // ----------------------------------------------------------------------------------------------
    
    // @Override
    // public void likeTweet(Long id, CredentialsDto credentialsDto) {
    //     // ToDo: Creates a “like” relationship between the tweet with the given id and the user whose credentials are provided by the request body. If the tweet is deleted or otherwise doesn’t exist, or if the given credentials do not match an active user in the database, an error should be sent. Following successful completion of the operation, no response body is sent.
    
    // }
        
    // @Override
    // public TweetResponseDto replyToTweet(Long id, CredentialsDto credentialsDto) {
    //     // ToDo: Creates a reply tweet to the tweet with the given id. The author of the newly-created tweet should match the credentials provided by the requestbody. If the given tweet is deleted or otherwise doesn’t exist, or if the given credentials do not match an active user in the database, an error should be sent in lieu of a response.
    
    //     // Because this creates a reply tweet, content is not optional. Additionally, notice that the inReplyTo property is not provided by the request. The server must create that relationship.
    
    //     // The response should contain the newly-created tweet.
    
    //     // IMPORTANT: when a tweet with content is created, the server must process the tweet’s content for @{username} mentions and #{hashtag} tags. There is no way to create hashtags or create mentions from the API, so this must be handled automatically!
    //     return null;
    // }
        
    // @Override
    // public TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto) {
    //     // ToDo: Creates a repost of the tweet with the given id. The author of the repost should match the credentials provided in the request body. If the given tweet is deleted or otherwise doesn’t exist, or the given credentials do not match an active user in the database, an error should be sent in lieu of a response.
    
    //     // Because this creates a repost tweet, content is not allowed. Additionally, notice that the repostOf property is not provided by the request. The server must create that relationship.
    
    //     // The response should contain the newly-created tweet.
    //     return null;
    // }
            
    // @Override
    // public List<HashtagResponseDto> getAllHashtagsByTweetId(Long id) {
    //     // ToDo: Retrieves the tags associated with the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.
    
    //     // IMPORTANT Remember that tags and mentions must be parsed by the server!
    //     return null;
    // }

    // @Override
    // public List<UserResponseDto> getAllLikesByTweetId(Long id) {
    //     // ToDo: Retrieves the active users who have liked the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.

    //     // Deleted users should be excluded from the response.
    //     return null;
    // }

    @Override
    public ContextDto getContextByTweetId(Long id) {
        // ToDo: Retrieves the context of the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.

        // IMPORTANT: While deleted tweets should not be included in the before and after properties of the result, transitive replies should. What that means is that if a reply to the target of the context is deleted, but there’s another reply to the deleted reply, the deleted reply should be excluded but the other reply should remain.
        return null;
    }

    @Override
    public List<TweetResponseDto> getRepliesByTweetId(Long id) {
        // ToDo: Retrieves the direct replies to the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.

        // Deleted replies to the tweet should be excluded from the response.
        return null;
    }

    @Override
    public List<TweetResponseDto> getRepostsByTweetId(Long id) {
        // ToDo: Retrieves the direct reposts of the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.

        //Deleted reposts of the tweet should be excluded from the response.
        return null;
    }

    // @Override
    // public List<UserResponseDto> getMentionsByTweetId(Long id) {
    //     // ToDo: Retrieves the users mentioned in the tweet with the given id. If that tweet is deleted or otherwise doesn’t exist, an error should be sent in lieu of a response.

    //     // Deleted users should be excluded from the response.

    //     // IMPORTANT Remember that tags and mentions must be parsed by the server!
    //     return null;
    // }
}
            
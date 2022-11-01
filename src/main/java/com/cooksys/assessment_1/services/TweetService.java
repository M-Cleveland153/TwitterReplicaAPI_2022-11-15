package com.cooksys.assessment_1.services;

import java.util.List;

import com.cooksys.assessment_1.dtos.ContextDto;
import com.cooksys.assessment_1.dtos.TweetRequestDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;

public interface TweetService {

    // ----------------------------------------------------------------------------------------------
    // --------------- Below methods commented out due to pending access to DTOs --------------------
    // ----------------------------------------------------------------------------------------------

    List<TweetResponseDto> getAllTweets();

    TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

    TweetResponseDto getTweetById(Long id);

    // TweetResponseDto deleteTweetById(Long id, CredentialsDto credentialsDto);

    // void likeTweet(Long id, CredentialsDto credentialsDto);
    
    // TweetResponseDto replyToTweet(Long id, CredentialsDto credentialsDto);
    
    // TweetResponseDto repostTweet(Long id, CredentialsDto credentialsDto);
    
    // List<HashtagResponseDto> getAllHashtagsByTweetId(Long id);

    // List<UserResponseDto> getAllLikesByTweetId(Long id);

    ContextDto getContextByTweetId(Long id);

    List<TweetResponseDto> getRepliesByTweetId(Long id);

    List<TweetResponseDto> getRepostsByTweetId(Long id);

    // List<UserResponseDto> getMentionsByTweetId(Long id);
    
}

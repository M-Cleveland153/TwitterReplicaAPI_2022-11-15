package com.cooksys.assessment_1.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.assessment_1.dtos.HashtagResponseDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.entities.Hashtag;
import com.cooksys.assessment_1.entities.Tweet;
import com.cooksys.assessment_1.mappers.HashtagMapper;
import com.cooksys.assessment_1.mappers.TweetMapper;
import com.cooksys.assessment_1.repositories.HashtagRepository;
import com.cooksys.assessment_1.repositories.TweetRepository;
import com.cooksys.assessment_1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService{
	
	private final HashtagMapper hashtagMapper;
	private final HashtagRepository hashtagRepository;
	private final TweetMapper tweetMapper;
	private final TweetRepository tweetRepository;
	
	private Hashtag getHashtag(String label) {
		Optional<Hashtag> optionalHashtag = hashtagRepository.findByLabelAndDeletedFalse(label);
		// exception logic goes here
		
		return optionalHashtag.get();
	}

	
	// 	ASSIGNED REQUIREMENT:
	//	Retrieves all hashtags tracked by the database.
	//		Response ['Hashtag']
	
	//	CURRENT STATUS:
	//	Times returned are mysteriously 89 minutes earlier than those shown in the database.
	// 	Everything else works
	@Override
	public List<HashtagResponseDto> getAllHashtags() {
		return hashtagMapper.entitiesToDtos(hashtagRepository.findAll());
	}

	// 	ASSIGNED REQUIREMENT:
	//	Retrieves all (non-deleted) tweets tagged with the given hashtag label. 
	//	The tweets should appear in reverse-chronological order. If no hashtag with the given 
	//		label exists, an error should be sent in lieu of a response.
//		A tweet is considered "tagged" by a hashtag if the tweet has content and the 
	//		hashtag's label appears in that content following a #
	//		Response: ['Tweet']
	
	//	CURRENT STATUS:
	//	Posted times show "2022-11-02T23:32:08.527+00:00" while the database shows "2022-11-02 18:32:48.74"
	//	Username shows null while the database shows that it is populated. 
	@Override
	public List<TweetResponseDto> getTweetsByHashtag(String label) {
		List<Tweet> tweets = tweetRepository.findAll();
		List<Tweet> taggedTweets = new ArrayList<>();
		for(Tweet tweet: tweets) {
			if(tweet.getContent() != null && tweet.getContent().contains("#" + label)) {
				taggedTweets.add(tweet);
			}
		}
		return tweetMapper.entitiesToDtos(taggedTweets);
	
	}

}

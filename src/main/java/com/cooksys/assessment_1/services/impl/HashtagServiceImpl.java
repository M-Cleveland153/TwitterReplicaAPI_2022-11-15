package com.cooksys.assessment_1.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cooksys.assessment_1.dtos.HashtagResponseDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.mappers.HashtagMapper;
import com.cooksys.assessment_1.repositories.HashtagRepository;
import com.cooksys.assessment_1.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService{
	
	private final HashtagMapper hashtagMapper;
	private final HashtagRepository hashtagRepository;

	@Override
	public List<HashtagResponseDto> getAllHashtags() {
		// TODO Retrieves all hashtags tracked by the database.
		//		Response ['Hashtag']

		return null;
	}

	@Override
	public List<TweetResponseDto> getTweetsByHashtag(String label) {
		// TODO Retrieves all (non-deleted) tweets tagged with the given hashtag label. The tweets should appear in reverse-chronological order. If no hashtag with the given label exists, an error should be sent in lieu of a response.
		//		A tweet is considered "tagged" by a hashtag if the tweet has content and the hashtag's label appears in that content following a #
		//		Response: ['Tweet']

		return null;
	}

}

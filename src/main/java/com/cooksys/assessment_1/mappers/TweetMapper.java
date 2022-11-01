package com.cooksys.assessment_1.mappers;

import java.util.List;

import com.cooksys.assessment_1.dtos.TweetRequestDto;
import com.cooksys.assessment_1.dtos.TweetResponseDto;
import com.cooksys.assessment_1.entities.Tweet;

public interface TweetMapper {
    
    Tweet DtoToEntity(TweetRequestDto tweetRequestDto);

    TweetResponseDto entityToDto(Tweet entity);

    List<TweetResponseDto> entitiesToDtos(List<Tweet> tweets);

    // ToDo: Find out how to map ContextDto

}

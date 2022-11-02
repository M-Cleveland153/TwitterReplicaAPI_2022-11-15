package com.cooksys.assessment_1.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cooksys.assessment_1.entities.Hashtag;

@Repository
public interface HashtagRepository {
	
	Optional<Hashtag> findById(Long id);

}

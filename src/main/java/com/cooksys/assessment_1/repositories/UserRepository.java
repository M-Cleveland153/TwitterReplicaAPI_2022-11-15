package com.cooksys.assessment_1.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.cooksys.assessment_1.entities.User;

@Repository
public interface UserRepository {

	Optional<User> findById(Long id);
}

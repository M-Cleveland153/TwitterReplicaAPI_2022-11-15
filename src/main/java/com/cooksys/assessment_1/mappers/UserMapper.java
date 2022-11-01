package com.cooksys.assessment_1.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.assessment_1.dtos.UserRequestDto;
import com.cooksys.assessment_1.dtos.UserResponseDto;
import com.cooksys.assessment_1.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	User requestDtoToEntity(UserRequestDto userRequestDto);
	
	UserResponseDto entityToDto(User user);
	
	List<UserResponseDto> entitiesToDtos(List<User> users);
}

package com.cooksys.assessment_1.dtos;

import java.sql.Timestamp;

import lombok.Data;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {

	private String username;

	private ProfileDto profileDto;

	private Timestamp joined;
}

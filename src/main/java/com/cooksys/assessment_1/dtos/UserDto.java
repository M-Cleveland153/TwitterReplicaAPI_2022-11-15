package com.cooksys.assessment_1.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDto {
	
	private CredentialsDto credentials;
	
	private ProfileDto profile;
}

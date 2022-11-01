package com.cooksys.assessment_1.dtos;

import com.cooksys.assessment_1.entities.Credentials;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetRequestDto {

    private String content;

    // Need access to CredentialsDTO
    // private CredentialsDto credentials;

}

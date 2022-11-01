package com.cooksys.assessment_1.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@Table(name="user_table")
public class User {

	@Id
	@GeneratedValue
	@Column(unique = true)
	private Long id;
	
	@Embedded
	private Credentials credential;
	
	private Timestamp joined;
	
	private boolean deleted;
	
	@Embedded
	private Profile profile;
	
	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	private List<Tweet> tweets;
}

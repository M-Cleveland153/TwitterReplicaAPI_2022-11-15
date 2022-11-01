package com.cooksys.assessment_1.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Hashtag {

	@Id
	@GeneratedValue
	private Long id;

	private String label;

	private Timestamp firstUsed;

	private Timestamp lastUsed;

	// May need to switch cascade type to persist and merge
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "hashtags")
    private List<Tweet> tweets;

}

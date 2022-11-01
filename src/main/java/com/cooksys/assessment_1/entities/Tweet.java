package com.cooksys.assessment_1.entities;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Tweet {
    
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Long id;

    @Column(nullable = false)
    private Long author;
    
    @Column(nullable = false)
    private Timestamp posted;

    private boolean deleted;

    private String content;

    @OneToMany(mappedBy = "tweet")
    private Tweet inReplyTo;

    private List<Tweet> replies;

    @OneToMany(mappedBy = "tweet")
    private Tweet repostOf;

    private List<Tweet> reposts;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

 // May need to switch cascade type to persist and merge
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "tweet_hashtags",
        joinColumns = { @JoinColumn(name = "tweet_id") },
        inverseJoinColumns = { @JoinColumn(name = "hashtag_id") })
    private Set<Hashtag> hashtags;

}

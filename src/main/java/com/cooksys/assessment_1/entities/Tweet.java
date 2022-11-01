package com.cooksys.assessment_1.entities;

import java.sql.Timestamp;
import java.util.List;

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

import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp posted;

    private boolean deleted;

    private String content;

    @ManyToOne
    private Tweet inReplyTo;

    @ManyToOne
    private Tweet repostOf;

 // May need to switch cascade type to persist and merge
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "tweet_hashtags",
        joinColumns = { @JoinColumn(name = "tweet_id") },
        inverseJoinColumns = { @JoinColumn(name = "hashtag_id") })
    private List<Hashtag> hashtags;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "likedTweets")
    private List<User> likedByUsers;
    
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "mentionedTweets")
    private List<User> mentionedByUsers;

}

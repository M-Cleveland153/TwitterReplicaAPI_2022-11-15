package com.cooksys.assessment_1.entities;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
    private int author;
    
    @Column(nullable = false)
    private Timestamp posted;

    private boolean deleted;

    private String content;

    private int inReplyTo;

    private int repostOf;

    // @ManyToOne
    // @JoinColumn(name = "author_id")
    // private User user;

    // @OneToMany(mappedBy = "tweet", cascade = {CascadeType.ALL})
    // private List<Hashtag> hashtags;

}

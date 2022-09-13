package com.velog.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postTagId;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "tag_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    public PostTag(Post post, Member member, Tag tag){
        this.post = post;
        this.member = member;
        this.tag = tag;
    }
}

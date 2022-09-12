package com.velog.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class PostTag extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postTagId;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Column(nullable = false)
    private Long refMemberId;
//    @JoinColumn(name = "member_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;

    @JoinColumn(name = "tag_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    public PostTag(Post post, Tag tag, Long refMemberId){
        this.post = post;
        this.tag = tag;
        this.refMemberId = refMemberId;
    }
}

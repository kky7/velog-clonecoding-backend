package com.velog.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false)
    private String imgUrl;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public Image(Post post, String imgUrl){
        this.post = post;
        this.imgUrl = imgUrl;
    }

    public void updateImgUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }
}

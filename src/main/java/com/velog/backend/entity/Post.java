package com.velog.backend.entity;

import com.velog.backend.dto.request.PostReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Post extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;

    @Column
    private String content;

    @Column
    private int likesNum;

    @ElementCollection
    @CollectionTable(name="img_url")
    private List<String> imgUrl = new ArrayList<>();

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTagList;

    public Post(PostReqDto postReqDto, Member member){
        this.title = postReqDto.getTitle();
        this.content = postReqDto.getContent();
        this.imgUrl = postReqDto.getImgUrl();
        this.member = member;
    }

}

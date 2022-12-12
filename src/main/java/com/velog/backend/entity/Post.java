package com.velog.backend.entity;

import com.velog.backend.dto.request.PostReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @Column(length = 4000)
    private String content;

    @Column
    private int likesNum;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> postTagList;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> imageList;

    public Post(PostReqDto postReqDto, Member member){
        this.title = postReqDto.getTitle();
        this.content = postReqDto.getContent();
        this.member = member;
    }

    public void update(PostReqDto postReqDto){
        String title = postReqDto.getTitle();
        String content = postReqDto.getContent();
        if(title != null){
            this.title = title;
        }
        if(content != null){
            this.content = content;
        }
    }

    public boolean validateMember(Long memberId) {
        Long thisMemberId = this.member.getMemberId();
        return !memberId.equals(thisMemberId);
    }

    public void like(){
        this.likesNum += 1;
    }

    public void unlike(){
        this.likesNum -= 1;
    }

}

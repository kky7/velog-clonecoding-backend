package com.velog.backend.dto.response;

import com.velog.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsResDto {
    private Long postId;
    private String nickname;
    private String profileUrl;
    private String title;
    private String content;
    private String imgUrl;
    private int likesNum;
    private Long commentsNum;
    private String date;

    public PostsResDto(Post post, Long commentsNum, String imgUrl, String dateFormat){
        this.postId = post.getPostId();
        this.nickname = post.getMember().getNickname();
        this.profileUrl = post.getMember().getProfileUrl();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = imgUrl;
        this.likesNum = post.getLikesNum();
        this.commentsNum = commentsNum;
        this.date = dateFormat;
    }
}

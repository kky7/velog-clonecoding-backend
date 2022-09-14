package com.velog.backend.dto.response;

import com.velog.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SearchPostResDto {

    private Long postId;
    private String nickname;
    private String profileUrl;
    private String title;
    private String content;
    private String imgUrl;
    private List<String> tag;
    private int likesNum;
    private Long commentsNum;
    private String date;

    public SearchPostResDto(Post post, Long commentsNum, String imgUrl, List<String> tag, String dateFormat) {
        this.postId = post.getPostId();
        this.nickname = post.getMember().getNickname();
        this.profileUrl = post.getMember().getProfileUrl();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = imgUrl;
        this.tag = tag;
        this.likesNum = post.getLikesNum();
        this.commentsNum = commentsNum;
        this.date = dateFormat;
    }
}

package com.velog.backend.dto.response;

import com.velog.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyPagePostResDto {

    private Long postId;
    private String title;
    private String content;
    private String imgUrl;
    private List<String> tag;
    private int likesNum;
    private Long commentsNum;
    private String date;

    public MyPagePostResDto(Post post, Long commentsNum, String imgUrl, List<String> tag, String dateFormat) {
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = imgUrl;
        this.tag = tag;
        this.likesNum = post.getLikesNum();
        this.commentsNum = commentsNum;
        this.date = dateFormat;
    }
}

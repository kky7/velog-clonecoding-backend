package com.velog.backend.dto.response;

import com.velog.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostResDto {
    private Long postId;
    private String title;
    private String content;
    private List<String> imgUrl;
    private List<String> tag;

    public PostResDto(Post post, List<String> imgUrls, List<String> tags){
        this.postId = post.getPostId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = imgUrls;
        this.tag = tags;
    }

}

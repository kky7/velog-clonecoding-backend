package com.velog.backend.dto.response;

import com.velog.backend.dto.request.PostReqDto;
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

    public PostResDto(Long postId, PostReqDto postReqDto){
        this.postId = postId;
        this.title = postReqDto.getTitle();
        this.content = postReqDto.getContent();
        this.imgUrl = postReqDto.getImgUrl();
        this.tag = postReqDto.getTag();
    }

}

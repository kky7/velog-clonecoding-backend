package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetPostDetailDto {
    private Long postId;
    private String nickname;
    private String velogTitle;
    private String profileUrl;
    private String introduction;
    private String title;
    private String content;
    private List<String> imgUrl;
    private List<String> tag;
    private int likesNum;
    private List<CommentInfoDto> commentsList;
    private String date;

    public GetPostDetailDto(Post post, Member authorOfPost, List<String> tag, List<CommentInfoDto> commentsList, String dateFormat){
        this.postId = post.getPostId();
        this.nickname = authorOfPost.getNickname();
        this.velogTitle = authorOfPost.getVelogTitle();
        this.profileUrl = authorOfPost.getProfileUrl();
        this.introduction = authorOfPost.getIntroduction();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.tag = tag;
        this.likesNum = post.getLikesNum();
        this.commentsList = commentsList;
        this.date = dateFormat;
    }
}

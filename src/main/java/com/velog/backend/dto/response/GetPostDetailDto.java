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

    public GetPostDetailDto(Post post, Member author, List<String> imgUrls ,List<String> tags, List<CommentInfoDto> comments, String dateFormat){
        this.postId = post.getPostId();
        this.nickname = author.getNickname();
        this.velogTitle = author.getVelogTitle();
        this.profileUrl = author.getProfileUrl();
        this.introduction = author.getIntroduction();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.imgUrl = imgUrls;
        this.tag = tags;
        this.likesNum = post.getLikesNum();
        this.commentsList = comments;
        this.date = dateFormat;
    }
}

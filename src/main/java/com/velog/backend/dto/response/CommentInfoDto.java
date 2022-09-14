package com.velog.backend.dto.response;

import com.velog.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentInfoDto {
    private Long commentId;
    private Long postId;
    private String nickname;
    private String profileUrl;
    private String content;
    private String date;

    public CommentInfoDto(Comment comment, String dateFormat){
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.nickname = comment.getMember().getNickname();
        this.profileUrl = comment.getMember().getProfileUrl();
        this.content = comment.getContent();
        this.date = dateFormat;
    }
}

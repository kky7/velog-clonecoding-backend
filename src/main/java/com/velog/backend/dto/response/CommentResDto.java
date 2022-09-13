package com.velog.backend.dto.response;

import com.velog.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentResDto {

    private Long commentId;
    private Long postId;
    private String profileUrl;
    private String nickname;
    private String content;
    private String date;

    public CommentResDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.profileUrl = comment.getMember().getProfileUrl();
        this.nickname = comment.getMember().getNickname();
        this.content = comment.getContent();
        this.date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(comment.getCreatedAt());

    }

}

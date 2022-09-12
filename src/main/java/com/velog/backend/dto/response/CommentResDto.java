package com.velog.backend.dto.response;

import com.velog.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentResDto {

    private Long commentId;
    private Long postId;
    private String content;
    private String date;
    private CommentMemberResDto responseDto;

    public CommentResDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
        this.date = String.valueOf(comment.getCreatedAt());
        this.responseDto = new CommentMemberResDto(comment.getMember());


    }

}

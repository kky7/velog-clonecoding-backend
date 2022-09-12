package com.velog.backend.dto.response;

import com.velog.backend.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentResponseDto {

    private Long commentId;
    private Long postId;
    private String content;
    private String date;
    private CommentMemberResponseDto responseDto;

    public CommentResponseDto (Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
        this.date = String.valueOf(comment.getCreatedAt());
        this.responseDto = new CommentMemberResponseDto(comment.getMember());


    }

}

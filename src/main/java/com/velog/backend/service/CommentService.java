package com.velog.backend.service;

import com.velog.backend.dto.request.CommentReqDto;
import com.velog.backend.dto.response.CommentResDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Comment;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.constant.exception.ErrorMsg;
import com.velog.backend.constant.exception.SuccessMsg;
import com.velog.backend.repository.CommentRepository;
import com.velog.backend.repository.PostRepository;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // 댓글 생성
    @Transactional
    public ResponseEntity<?> createComment(CommentReqDto requestDto, UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();
        Post post = isPresentPost(requestDto.getPostId());
        if (null == post) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Comment comment = new Comment(post, member, requestDto);
        commentRepository.save(comment);

        CommentResDto commentResDto = new CommentResDto(comment);
        GlobalResDto<CommentResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.CREATE_SUCCESS, commentResDto);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 댓글 수정
    @Transactional
    public ResponseEntity<?> updateComment(Long commentId, CommentReqDto requestDto, UserDetailsImpl userDetails) {

        Comment comment = isPresentComment(commentId);
        if (null == comment ) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.COMMENT_NOT_FOUND);
        }

        Member member = comment.getMember();
        Long memberId = member.getMemberId();
        if (!memberId.equals(userDetails.getMember().getMemberId())) {
            return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        comment.update(requestDto);

        CommentResDto commentResDto = new CommentResDto(comment);
        GlobalResDto<CommentResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.UPDATE_SUCCESS, commentResDto);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 댓글 삭제
    @Transactional
    public ResponseEntity<?> deleteComment(Long commentId, UserDetailsImpl userDetails) {

        Comment comment = isPresentComment(commentId);
        if (null == comment ) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.COMMENT_NOT_FOUND);
        }

        Member member = comment.getMember();
        Long memberId = member.getMemberId();
        if (!memberId.equals(userDetails.getMember().getMemberId())) {
            return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        commentRepository.delete(comment);
        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.DELETE_SUCCESS);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 댓글 ID 유무 확인
    @Transactional(readOnly = true)
    public Comment isPresentComment(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

    // 게시글 ID 유무 확인
    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }
}

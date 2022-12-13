package com.velog.backend.service;

import com.velog.backend.dto.response.LikesResDto;
import com.velog.backend.entity.Likes;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.constant.exception.ErrorMsg;
import com.velog.backend.constant.exception.SuccessMsg;
import com.velog.backend.repository.LikesRepository;
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
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostRepository postRepository;

    @Transactional
    public ResponseEntity<?> getLikes(LikesResDto likesResDto, UserDetailsImpl userDetails) {

        Member member = userDetails.getMember();
        if (null == member) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.MEMBER_NOT_FOUND);
        }
        Post post = getPost(likesResDto);
        if (null == post) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Likes checkLikes = likesRepository.findByMemberAndPost(member, post);

        if (null == checkLikes) {
            Likes likes = new Likes(member, post);
            likesRepository.save(likes);
            post.like();
            return ServiceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.LIKE_SUCCESS);
        }

        likesRepository.deleteById(checkLikes.getLikeId());
        post.unlike();
        return ServiceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.LIKE_CANCEL);

    }

    // PostId 가져오기
    private Post getPost(LikesResDto likesResDto) {
        Optional<Post> optionalPost = postRepository.findById(likesResDto.getPostId());
        return optionalPost.orElse(null);
    }
}

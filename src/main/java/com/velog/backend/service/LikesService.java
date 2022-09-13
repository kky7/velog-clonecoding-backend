package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.LikesResDto;
import com.velog.backend.entity.Likes;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.exception.ErrorMsg;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.repository.LikesRepository;
import com.velog.backend.repository.MemberRepository;
import com.velog.backend.repository.PostRepository;
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
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;


    @Transactional
    public ResponseEntity<?> likes(LikesResDto likesResDto) {

        Member member = getMember(likesResDto);
        if (null == member) {
            return dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.MEMBER_NOT_FOUND);
        }
        Post post = getPost(likesResDto);
        if (null == post) {
            return dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Likes checkLikes = likesRepository.findByMemberAndPost(member, post);

        if (null == checkLikes) {
            Likes likes = new Likes(member, post);
            likesRepository.save(likes);
            return dataNullResponse(HttpStatus.OK, SuccessMsg.LIKE_SUCCESS);
        }

        likesRepository.deleteById(checkLikes.getLikeId());
        return dataNullResponse(HttpStatus.OK, SuccessMsg.LIKE_CANCEL);
    }

    // MemberId 가져오기
    private Member getMember(LikesResDto likesResDto) {
        Optional<Member> optionalMember = memberRepository.findById(likesResDto.getMemberId());
        return optionalMember.orElse(null);
    }

    // PostId 가져오기
    private Post getPost(LikesResDto likesResDto) {
        Optional<Post> optionalPost = postRepository.findById(likesResDto.getPostId());
        return optionalPost.orElse(null);
    }

    private ResponseEntity<?> dataNullResponse(HttpStatus httpStatus, String msg){
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus,msg);
        return new ResponseEntity<>(globalResDto,httpStatus);
    }
}

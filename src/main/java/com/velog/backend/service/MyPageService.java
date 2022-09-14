package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.MyPageLikePostResDto;
import com.velog.backend.dto.response.MyPagePostResDto;
import com.velog.backend.entity.Likes;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.exception.ErrorMsg;
import com.velog.backend.repository.*;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostTagRepository postTagRepository;
    private final LikesRepository likesRepository;
    private final ServiceUtil serviceUtil;

    // 멤버별 게시물 전체 조회
    @Transactional
    public ResponseEntity<?> getAllPostByMember(String nickname) {

        List<MyPagePostResDto> myPagePostResDtoList = new ArrayList<>();

        Member member = memberRepository.findAllByNickname(nickname);

        if (null == member) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.MEMBER_NOT_FOUND);
        }

        List<Post> postList = postRepository.findAllByMember(member);

        for (Post post : postList) {
            Long commentsNum = commentRepository.countByPost(post);

            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if (!imgUrlList.isEmpty()) {
                imgUrl = imgUrlList.get(0);
            }

            List<PostTag> postTagList = postTagRepository.findAllByPost(post);
            List<String> tagNameList = new ArrayList<>();

            for (PostTag postTag : postTagList) {
                String tagName = postTag.getTag().getTagName();
                tagNameList.add(tagName);
            }

            String velogTitle = post.getMember().getVelogTitle();

            MyPagePostResDto myPagePostResDto = new MyPagePostResDto(post, velogTitle, commentsNum, imgUrl, tagNameList, serviceUtil.getDataFormatOfPost(post));
            myPagePostResDtoList.add(myPagePostResDto);
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, myPagePostResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 내가 좋아요한 게시글 전체 조회
    @Transactional
    public ResponseEntity<?> getMyLikePost(UserDetailsImpl userDetails) {

        List<MyPageLikePostResDto> myPageLikePostResDtoList = new ArrayList<>();
        List<Post> postList = new ArrayList<>();
        Long memberId = userDetails.getMember().getMemberId();

        List<Likes> myLikeList = likesRepository.findAllByMember_MemberId(memberId);

        if (null == myLikeList) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        for (Likes likes : myLikeList) {
            postList.add(likes.getPost());
        }

        for (Post post : postList) {
            Long commentsNum = commentRepository.countByPost(post);

            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if (!imgUrlList.isEmpty()) {
                imgUrl = imgUrlList.get(0);
            }

            MyPageLikePostResDto myPageLikePostResDto = new MyPageLikePostResDto(post, commentsNum, imgUrl, serviceUtil.getDataFormatOfPost(post));
            myPageLikePostResDtoList.add(myPageLikePostResDto);
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, myPageLikePostResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }
}

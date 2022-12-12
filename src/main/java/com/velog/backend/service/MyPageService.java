package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.MyPageLikePostResDto;
import com.velog.backend.dto.response.MyPagePostResDto;
import com.velog.backend.dto.response.MyPageResDto;
import com.velog.backend.entity.*;
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
    private final ImageRepository imageRepository;
    private final ServiceUtil serviceUtil;

    // 멤버별 게시물 전체 조회
    @Transactional
    public ResponseEntity<?> getAllPostByMember(String nickname) {

        Member member = memberRepository.findAllByNickname(nickname);

        if (null == member) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.MEMBER_NOT_FOUND);
        }

        List<PostTag> postTagListByMember = postTagRepository.findAllByMember(member);
        List<String> allTagNameList = new ArrayList<>();
        List<Long> numOfTagList = new ArrayList<>();
        for(PostTag postTag : postTagListByMember){
            Tag tag = postTag.getTag();
            String tagName = tag.getTagName();
            if(!allTagNameList.contains(tagName)){
                allTagNameList.add(tagName);
                Long numOfTag = postTagRepository.countByMemberAndTag(member,tag);
                numOfTagList.add(numOfTag);
            }
        }

        List<Post> postList = postRepository.findAllByMember(member);

        List<MyPagePostResDto> myPagePostResDtoList = new ArrayList<>();

        for (Post post : postList) {
            Long commentsNum = commentRepository.countByPost(post);

            String imgUrl = imageRepository.findAllByPost_PostId(post.getPostId());

            List<PostTag> postTagList = postTagRepository.findAllByPost(post);
            List<String> tagNameList = new ArrayList<>();

            for (PostTag postTag : postTagList) {
                String tagName = postTag.getTag().getTagName();
                tagNameList.add(tagName);
            }


            MyPagePostResDto myPagePostResDto = new MyPagePostResDto(post, commentsNum, imgUrl, tagNameList, serviceUtil.getDataFormat(post.getCreatedAt()));
            myPagePostResDtoList.add(myPagePostResDto);
        }

        MyPageResDto myPageResDto = new MyPageResDto(member, allTagNameList, numOfTagList, myPagePostResDtoList);

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, myPageResDto);

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

            String imgUrl = imageRepository.findAllByPost_PostId(post.getPostId());

            MyPageLikePostResDto myPageLikePostResDto = new MyPageLikePostResDto(post, commentsNum, imgUrl, serviceUtil.getDataFormat(post.getCreatedAt()));
            myPageLikePostResDtoList.add(myPageLikePostResDto);
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, myPageLikePostResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }
}

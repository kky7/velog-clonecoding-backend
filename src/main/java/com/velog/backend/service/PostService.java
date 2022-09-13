package com.velog.backend.service;

import com.velog.backend.dto.request.PostReqDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.PostResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.repository.PostRepository;
import com.velog.backend.repository.PostTagRepository;
import com.velog.backend.repository.TagRepository;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PostService  {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final ServiceUtil serviceUtil;

    // 게시글 작성
    @Transactional
    public ResponseEntity<?> createPost(PostReqDto postReqDto, UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        Post post = new Post(postReqDto, member);
        Post savePost = postRepository.save(post);
        List<String> tagList = postReqDto.getTag();

        if(tagList != null){
            for (String tagName : tagList){
                Tag tagInDB = tagRepository.findByTagName(tagName);
                if(tagInDB == null){
                    Tag tag = new Tag(tagName);
                    Tag saveTag = tagRepository.save(tag);
                    PostTag postTag = new PostTag(savePost, member, saveTag);
                    postTagRepository.save(postTag);
                } else{
                    PostTag postTag = new PostTag(savePost, member, tagInDB);
                    postTagRepository.save(postTag);
                }
            }
        }

        PostResDto postResDto = new PostResDto(savePost.getPostId(),postReqDto);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.POST_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 게시글 수정
//    @Transactional
//    public ResponseEntity<?> updatePost(Long postId, PostReqDto postReqDto, UserDetailsImpl userDetails){
//
//        Optional<Post> optionalPost = postRepository.findByPostId(postId);
//        if(optionalPost.isEmpty()){
//            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
//        }
//
//        Member member = userDetails.getMember();
//        Long memberId = member.getMemberId();
//        List<String> tagList = postReqDto.getTag();
//
//
//    }

}

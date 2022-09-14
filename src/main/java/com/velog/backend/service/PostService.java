package com.velog.backend.service;

import com.velog.backend.dto.request.PostReqDto;
import com.velog.backend.dto.response.*;
import com.velog.backend.entity.*;
import com.velog.backend.exception.ErrorMsg;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.repository.CommentRepository;
import com.velog.backend.repository.PostRepository;
import com.velog.backend.repository.PostTagRepository;
import com.velog.backend.repository.TagRepository;
import com.velog.backend.security.user.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommentRepository commentRepository;
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
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.CREATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<?> updatePost(Long postId, PostReqDto postReqDto, UserDetailsImpl userDetails){

        Post post = isPresentPost(postId);
        if (null == post) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();

        if(post.validateMember(member.getMemberId())) {
            return serviceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        List<String> afterTagList = postReqDto.getTag();
        List<PostTag> beforePostTagList = postTagRepository.findAllByPost(post);

        int beforeTagSize = beforePostTagList.size();
        int afterTagSize = afterTagList.size();

        if(beforeTagSize < afterTagSize){
            updatePostTag(beforeTagSize, beforePostTagList, afterTagList);

            for(int i=beforeTagSize; i<afterTagSize; i++){
                // 새로 생성
                String finalNewTagName = afterTagList.get(i);
                Tag tagCheck = tagRepository.findByTagName(finalNewTagName);
                if(tagCheck == null){
                    Tag tag = new Tag(finalNewTagName);
                    Tag saveTag = tagRepository.save(tag);
                    PostTag postTag = new PostTag(post, member, saveTag);
                    postTagRepository.save(postTag);
                } else {
                    PostTag postTag = new PostTag(post, member, tagCheck);
                    postTagRepository.save(postTag);
                }
            }

        } else if(beforeTagSize == afterTagSize){
            updatePostTag(beforeTagSize,beforePostTagList,afterTagList);
        } else {
            updatePostTag(afterTagSize,beforePostTagList,afterTagList);

            for(int i=afterTagSize; i<beforeTagSize;i++){
                // 남은거 삭제
                PostTag redidualPostTag = beforePostTagList.get(i);
                Tag tagOfResidual = redidualPostTag.getTag();
                postTagRepository.delete(redidualPostTag);
                List<PostTag> postTagList = postTagRepository.findAllByTag(tagOfResidual);
                if(postTagList.size() < 1){
                    tagRepository.delete(tagOfResidual);
                }
            }
        }

        post.update(postReqDto);

        PostResDto postResDto = new PostResDto(post.getPostId(),postReqDto);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.UPDATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<?> deletePost(Long postId, UserDetailsImpl userDetails){
        Post post = isPresentPost(postId);
        if (null == post) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();

        if(post.validateMember(member.getMemberId())) {
            return serviceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        List<PostTag> postTagList = postTagRepository.findAllByPost(post);
        List<Tag> tagList = new ArrayList<>();

        for(PostTag postTag : postTagList){
            Tag tag = postTag.getTag();
            tagList.add(tag);
        }

        postRepository.delete(post);

        for (Tag tag : tagList){
            List<PostTag> postTagListByTag = postTagRepository.findAllByTag(tag);
            if(postTagListByTag.size() < 1){tagRepository.delete(tag);}
        }

        return serviceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.DELETE_SUCCESS);
    }

    @Transactional
    public ResponseEntity<?> getPostDetail(Long postId){
        Post post = isPresentPost(postId);
        if (null == post) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        List<PostTag> postTagList = postTagRepository.findAllByPost(post);
        List<String> tagNameList = new ArrayList<>();

        for(PostTag postTag : postTagList){
            String tagName = postTag.getTag().getTagName();
            tagNameList.add(tagName);
        }

        List<CommentInfoDto> commentInfoDtoList = new ArrayList<>();

        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);

        for(Comment comment : commentList){
            CommentInfoDto commentInfoDto = new CommentInfoDto(comment, serviceUtil.getDataFormatOfComment(comment));
            commentInfoDtoList.add(commentInfoDto);
        }

        String postDateFormat = serviceUtil.getDataFormatOfPost(post);

        GetPostDetailDto getPostDetailDto = new GetPostDetailDto(post, post.getMember(), tagNameList,commentInfoDtoList, postDateFormat);

        GlobalResDto<GetPostDetailDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, getPostDetailDto);
        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> getAllPostDesc(){
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();

        List<GetAllPostDto> getAllPostDtoList = new ArrayList<>();

        for(Post post : postList){
            Long commentsNum = commentRepository.countByPost(post);
            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if(!imgUrlList.isEmpty()){
                imgUrl = imgUrlList.get(0);
            }
            GetAllPostDto getAllPostDto = new GetAllPostDto(post, commentsNum, imgUrl,serviceUtil.getDataFormatOfPost(post));
            getAllPostDtoList.add(getAllPostDto);
        }

        GlobalResDto<List<GetAllPostDto>> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, getAllPostDtoList);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    public void updatePostTag(int size, List<PostTag> beforePostTagList, List<String> afterTagList){
        for (int i=0; i<size; i++){
            String newTagName = afterTagList.get(i);
            PostTag beforePostTag = beforePostTagList.get(i);
            Tag beforeTag = beforePostTag.getTag();

            Tag tagInDB = tagRepository.findByTagName(newTagName);

            if(tagInDB == null){
                Tag tag = new Tag(newTagName);
                Tag saveTag = tagRepository.save(tag);
                beforePostTag.updateTag(saveTag);
            } else{
                beforePostTag.updateTag(tagInDB);
            }

            List<PostTag> postTagList = postTagRepository.findAllByTag(beforeTag);
            if(postTagList.size() < 1){tagRepository.delete(beforeTag);}
        }
    }

    // 게시글 ID 유무 확인
    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

}

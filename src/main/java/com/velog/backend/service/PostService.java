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
        List<String> tagNames = postReqDto.getTag();

        if(tagNames != null){
            for (String tagName : tagNames){
                savePostTag(savePost, member, tagName);
            }
        }

        PostResDto postResDto = new PostResDto(post, tagNames);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.CREATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<?> updatePost(Long postId, PostReqDto postReqDto, UserDetailsImpl userDetails){

        Post post = findPostById(postId);
        if (post == null) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();
        if(post.validateMember(member.getMemberId())) {
            return serviceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }


        if(postReqDto.getTag() == null){
            post.update(postReqDto);
            List<String> curTagListInDB = serviceUtil.getTagNamesFromPostTag(post);
            PostResDto postResDto = new PostResDto(post, curTagListInDB);
            GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.UPDATE_SUCCESS, postResDto);
            return new ResponseEntity<>(globalResDto, HttpStatus.OK);
        }

        List<String> afterTags = postReqDto.getTag();
        List<PostTag> beforePostTags = postTagRepository.findAllByPost(post);
        int beforeTagSize = beforePostTags.size();
        int afterTagSize = afterTags.size();

        if(beforeTagSize < afterTagSize){
            updatePostTag(beforeTagSize, beforePostTags, afterTags);

            for(int i = beforeTagSize; i < afterTagSize; i++){
                String tagName = afterTags.get(i);
                savePostTag(post, member, tagName);
            }

        } else if(beforeTagSize == afterTagSize){
            updatePostTag(beforeTagSize, beforePostTags, afterTags);
        } else {
            updatePostTag(afterTagSize, beforePostTags, afterTags);

            for(int i=afterTagSize; i<beforeTagSize;i++){
                // 남은거 삭제
                PostTag redidualPostTag = beforePostTags.get(i);
                Tag residualTag = redidualPostTag.getTag();
                postTagRepository.delete(redidualPostTag);
                if(postTagRepository.countByTag(residualTag) < 1){tagRepository.delete(residualTag);}
            }
        }

        post.update(postReqDto);
        List<String> curTagListInDB = serviceUtil.getTagNamesFromPostTag(post);
        PostResDto postResDto = new PostResDto(post,curTagListInDB);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.UPDATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);

    }

    // 게시글 삭제
    @Transactional
    public ResponseEntity<?> deletePost(Long postId, UserDetailsImpl userDetails){
        Post post = findPostById(postId);
        if (post == null) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();

        if(post.validateMember(member.getMemberId())) {
            return serviceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        List<PostTag> postTags = postTagRepository.findAllByPost(post);
        List<Tag> tags = new ArrayList<>();

        for(PostTag postTag : postTags){
            Tag tag = postTag.getTag();
            tags.add(tag);
        }

        postRepository.delete(post);

        for (Tag tag : tags){
            if(postTagRepository.countByTag(tag) < 1){tagRepository.delete(tag);}
        }

        return serviceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.DELETE_SUCCESS);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPostDetail(Long postId){
        Post post = findPostById(postId);
        if (null == post) {
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        List<String> tagNameList = serviceUtil.getTagNamesFromPostTag(post);

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

    // 메인 전체 게시글 목록 최신순 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllPostDesc(){
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();

        List<PostsResDto> postsResDtoList = new ArrayList<>();

        for(Post post : postList){
            Long commentsNum = commentRepository.countByPost(post);
            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if(!imgUrlList.isEmpty()){
                imgUrl = imgUrlList.get(0);
            }
            PostsResDto postsResDto = new PostsResDto(post, commentsNum, imgUrl, serviceUtil.getDataFormatOfPost(post));
            postsResDtoList.add(postsResDto);
        }

        GlobalResDto<List<PostsResDto>> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, postsResDtoList);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    @Transactional
    public void savePostTag(Post post, Member member, String tagName){
        Tag tagInDB = tagRepository.findByTagName(tagName);
        if(tagInDB == null){
            Tag saveTag = tagRepository.save(new Tag(tagName));
            PostTag postTag = new PostTag(post, member, saveTag);
            postTagRepository.save(postTag);
        } else {
            PostTag postTag = new PostTag(post, member, tagInDB);
            postTagRepository.save(postTag);
        }
    }

    @Transactional
    public void updatePostTag(int size, List<PostTag> beforePostTags, List<String> afterTags){
        for (int i = 0; i < size; i++){
            String newTagName = afterTags.get(i);
            PostTag beforePostTag = beforePostTags.get(i);
            Tag beforeTag = beforePostTag.getTag();
            Tag tagInDB = tagRepository.findByTagName(newTagName);

            if(tagInDB == null){
                Tag saveTag = tagRepository.save(new Tag(newTagName));
                beforePostTag.updateTag(saveTag);
            } else{
                beforePostTag.updateTag(tagInDB);
            }

            if(postTagRepository.countByTag(beforeTag) < 1){tagRepository.delete(beforeTag);}
        }
    }

    // 게시글 ID 유무 확인
    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

}

package com.velog.backend.service;

import com.velog.backend.dto.request.PostReqDto;
import com.velog.backend.dto.response.*;
import com.velog.backend.entity.*;
import com.velog.backend.constant.response.ErrorMsg;
import com.velog.backend.constant.response.SuccessMsg;
import com.velog.backend.repository.*;
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
    private final ImageRepository imageRepository;
    private final ServiceUtil serviceUtil;

    // 게시글 작성
    @Transactional
    public ResponseEntity<?> createPost(PostReqDto postReqDto, UserDetailsImpl userDetails){
        Member member = userDetails.getMember();
        Post post = new Post(postReqDto, member);
        Post savePost = postRepository.save(post);
        List<String> tagNames = postReqDto.getTag();
        List<String> imgUrls = postReqDto.getImgUrl();

        if(tagNames != null){
            for (String tagName : tagNames){
                savePostTag(savePost, member, tagName);
            }
        }

        if(imgUrls != null){
            for(String imgUrl: imgUrls){
                imageRepository.save(new Image(savePost, imgUrl));
            }
        }

        PostResDto postResDto = new PostResDto(post, imgUrls, tagNames);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.CREATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 게시글 수정
    @Transactional
    public ResponseEntity<?> updatePost(Long postId, PostReqDto postReqDto, UserDetailsImpl userDetails){

        Post post = findPostById(postId);
        if (post == null) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();
        if(post.validateMember(member.getMemberId())) {
            return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
        }

        List<String> newTagNames = postReqDto.getTag();
        if(newTagNames != null){
            updatePostTagAndTag(newTagNames, post, member);
        }

        List<String> newImgUrls = postReqDto.getImgUrl();
        if(newImgUrls != null){
            updateImage(newImgUrls, post);
        }

        post.update(postReqDto);
        PostResDto postResDto = new PostResDto(post, newImgUrls, newTagNames);
        GlobalResDto<PostResDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.UPDATE_SUCCESS, postResDto);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    // 게시글 삭제
    @Transactional
    public ResponseEntity<?> deletePost(Long postId, UserDetailsImpl userDetails){
        Post post = findPostById(postId);
        if (post == null) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        Member member = userDetails.getMember();

        if(post.validateMember(member.getMemberId())) {
            return ServiceUtil.dataNullResponse(HttpStatus.FORBIDDEN, ErrorMsg.MEMBER_NOT_MATCHED);
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

        return ServiceUtil.dataNullResponse(HttpStatus.OK, SuccessMsg.DELETE_SUCCESS);
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getPostDetail(Long postId){
        Post post = findPostById(postId);
        if (post == null) {
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.POST_NOT_FOUND);
        }

        List<String> tagNames = serviceUtil.getTagNamesFromPostTag(post);
        List<String> imgUrls = imageRepository.findAllByPostJPQL(post);

        List<CommentInfoDto> commentInfoDtos = new ArrayList<>();
        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
        for(Comment comment : commentList){
            CommentInfoDto commentInfoDto = new CommentInfoDto(comment, ServiceUtil.getDataFormat(comment.getCreatedAt()));
            commentInfoDtos.add(commentInfoDto);
        }

        String postDateFormat = ServiceUtil.getDataFormat(post.getCreatedAt());

        GetPostDetailDto getPostDetailDto = new GetPostDetailDto(post, post.getMember(), imgUrls, tagNames, commentInfoDtos, postDateFormat);
        GlobalResDto<GetPostDetailDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, getPostDetailDto);
        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

    // 메인 전체 게시글 목록 최신순 조회
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllPostDesc(){
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        List<PostsResDto> postsResDtos = new ArrayList<>();

        for(Post post : posts){
            Long commentsNum = commentRepository.countByPost(post);
            String imgUrl = imageRepository.findAllByPost_PostId(post.getPostId());
            PostsResDto postsResDto = new PostsResDto(post, commentsNum, imgUrl, ServiceUtil.getDataFormat(post.getCreatedAt()));
            postsResDtos.add(postsResDto);
        }

        GlobalResDto<List<PostsResDto>> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, postsResDtos);
        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

    @Transactional
    public void updatePostTagAndTag(List<String> newTagNames, Post post, Member member){
        List<PostTag> oldPostTags = postTagRepository.findAllByPost(post);
        int oldTagNum = oldPostTags.size();
        int newTageNum = newTagNames.size();

        if(oldTagNum < newTageNum){
            exchangePostTag(oldTagNum, oldPostTags, newTagNames);

            for(int i = oldTagNum; i < newTageNum; i++){
                savePostTag(post, member, newTagNames.get(i));
            }

        } else if(oldTagNum == newTageNum){
            exchangePostTag(oldTagNum, oldPostTags, newTagNames);
        } else {
            exchangePostTag(newTageNum, oldPostTags, newTagNames);

            for(int i = newTageNum; i < oldTagNum; i++){
                // 남은거 삭제
                PostTag redidualPostTag = oldPostTags.get(i);
                Tag residualTag = redidualPostTag.getTag();
                postTagRepository.delete(redidualPostTag);
                if(postTagRepository.countByTag(residualTag) < 1){tagRepository.delete(residualTag);}
            }
        }
    }

    @Transactional
    public void updateImage(List<String> newImgUrls, Post post){
        List<Image> oldImages = imageRepository.findAllByPost(post);
        int oldImgNum = oldImages.size();
        int newImgNum = newImgUrls.size();

        if(oldImgNum < newImgNum){
            exchangeImage(oldImgNum, oldImages, newImgUrls);

            for(int i = oldImgNum; i < newImgNum; i++){
                imageRepository.save(new Image(post, newImgUrls.get(i)));
            }
        } else if(oldImgNum == newImgNum){
            exchangeImage(oldImgNum, oldImages, newImgUrls);
        } else {
            exchangeImage(newImgNum, oldImages, newImgUrls);

            for(int i = newImgNum; i < oldImgNum; i++){
                imageRepository.delete(oldImages.get(i));
            }
        }
    }

    @Transactional
    public void exchangePostTag(int size, List<PostTag> oldPostTags, List<String> newTagNames){
        for (int i = 0; i < size; i++){
            String newTagName = newTagNames.get(i);
            PostTag oldPostTag = oldPostTags.get(i);
            Tag beforeTag = oldPostTag.getTag();
            Tag tagInDB = tagRepository.findByTagName(newTagName);

            if(tagInDB == null){
                Tag saveTag = tagRepository.save(new Tag(newTagName));
                oldPostTag.updateTag(saveTag);
            } else{
                oldPostTag.updateTag(tagInDB);
            }

            if(postTagRepository.countByTag(beforeTag) < 1){tagRepository.delete(beforeTag);}
        }
    }

    @Transactional
    public void exchangeImage(int size, List<Image> oldImages, List<String> newImgUrls){
        for(int i = 0; i < size; i++){
            String newImgUrl = newImgUrls.get(i);
            Image oldImage = oldImages.get(i);
            oldImage.updateImgUrl(newImgUrl);
        }
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

    // 게시글 ID 유무 확인
    @Transactional(readOnly = true)
    public Post findPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }

}

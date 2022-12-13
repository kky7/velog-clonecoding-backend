package com.velog.backend.service;

import com.velog.backend.dto.response.TagSearchPostsDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.TagSearchPostDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import com.velog.backend.constant.response.ErrorMsg;
import com.velog.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;
    private final ServiceUtil serviceUtil;

    @Transactional(readOnly = true)
    public ResponseEntity<?> tagSearch(String nickname, String searchTagName){
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (member == null){
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.BLOG_NOT_FOUND);
        }

        Tag tag = tagRepository.findByTagName(searchTagName);

        if(tag == null){
            return ServiceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.TAG_NOT_FOUND);
        }

        List<PostTag> postTags = postTagRepository.findAllByMemberAndTagOrderByPostDesc(member, tag);

        List<TagSearchPostDto> tagSearchPostDtos = new ArrayList<>();
        for(PostTag postTag : postTags){
            Post post = postTag.getPost();

            List<String> tagNames = serviceUtil.getTagNamesFromPostTag(post);
            List<String> imgUrls = imageRepository.findAllByPostJPQL(post);
            String imgUrl = null;
            if(!imgUrls.isEmpty()){
                imgUrl = imgUrls.get(0);
            }
            Long commentsNum = commentRepository.countByPost(post);
            TagSearchPostDto tagSearchPostDto = new TagSearchPostDto(post, tagNames, imgUrl, commentsNum, ServiceUtil.getDataFormat(post.getCreatedAt()));
            tagSearchPostDtos.add(tagSearchPostDto);
        }

        TagSearchPostsDto tagSearchPostsDto = new TagSearchPostsDto(member, tagSearchPostDtos);
        GlobalResDto<TagSearchPostsDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, tagSearchPostsDto);
        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

}

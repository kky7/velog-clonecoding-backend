package com.velog.backend.service;

import com.velog.backend.dto.response.TagSearchPostsDto;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.TagSearchPostDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import com.velog.backend.exception.ErrorMsg;
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
    private final ServiceUtil serviceUtil;

    @Transactional(readOnly = true)
    public ResponseEntity<?> tagSearch(String nickname, String searchTagName){
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (member == null){
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.BLOG_NOT_FOUND);
        }

        Tag tag = tagRepository.findByTagName(searchTagName);

        if(tag == null){
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.TAG_NOT_FOUND);
        }

        List<PostTag> postTagList = postTagRepository.findAllByMemberAndTagOrderByPostDesc(member, tag);

        List<TagSearchPostDto> tagSearchPostDtoList = new ArrayList<>();
        for(PostTag postTag : postTagList){
            Post post = postTag.getPost();

            List<String> tagNameList = serviceUtil.getTagNameListFromPostTag(post);
            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if(!imgUrlList.isEmpty()){
                imgUrl = imgUrlList.get(0);
            }
            Long commentsNum = commentRepository.countByPost(post);
            TagSearchPostDto tagSearchPostDto = new TagSearchPostDto(post, tagNameList, imgUrl, commentsNum, serviceUtil.getDataFormatOfPost(post));
            tagSearchPostDtoList.add(tagSearchPostDto);
        }

        TagSearchPostsDto tagSearchPostsDto = new TagSearchPostsDto(member, tagSearchPostDtoList);
        GlobalResDto<TagSearchPostsDto> globalResDto = new GlobalResDto<>(HttpStatus.OK, null, tagSearchPostsDto);
        return new ResponseEntity<>(globalResDto,HttpStatus.OK);
    }

}

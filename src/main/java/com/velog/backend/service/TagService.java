package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Member;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.entity.Tag;
import com.velog.backend.exception.ErrorMsg;
import com.velog.backend.repository.MemberRepository;
import com.velog.backend.repository.PostRepository;
import com.velog.backend.repository.PostTagRepository;
import com.velog.backend.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PostRepository postRepository;
    private final ServiceUtil serviceUtil;

    @Transactional
    public ResponseEntity<?> tagSearchInblog(String nickname, String tagName){
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        if (member == null){
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.BLOG_NOT_FOUND);
        }

        Tag tag = tagRepository.findByTagName(tagName);

        if(tag == null){
            return serviceUtil.dataNullResponse(HttpStatus.NOT_FOUND, ErrorMsg.TAG_NOT_FOUND);
        }

        List<PostTag> postTagList = postTagRepository.findAllByTagAndRefMemberIdOrderByCreatedAtDesc(tag,member.getMemberId());

        for(PostTag postTag : postTagList){
            Post post = postTag.getPost();
            System.out.println(post.getPostId());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}

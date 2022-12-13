package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.SearchPostResDto;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.constant.response.SuccessMsg;
import com.velog.backend.repository.CommentRepository;
import com.velog.backend.repository.ImageRepository;
import com.velog.backend.repository.PostRepository;
import com.velog.backend.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class SearchService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostTagRepository postTagRepository;
    private final ImageRepository imageRepository;

    // 게시글 검색
    @Transactional
    public ResponseEntity<?> searchPost(String searchWord) {
        List<Post> postList = postRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(searchWord, searchWord);
        List<SearchPostResDto> searchPostResDtoList = new ArrayList<>();

        for (Post post : postList) {

            Long commentsNum = commentRepository.countByPost(post);

            String imgUrl = imageRepository.findAllByPost_PostId(post.getPostId());

            List<PostTag> postTagList = postTagRepository.findAllByPost(post);
            List<String> tagNameList = new ArrayList<>();

            for (PostTag postTag : postTagList) {
                String tagName = postTag.getTag().getTagName();
                tagNameList.add(tagName);
            }

            SearchPostResDto searchPostResDto = new SearchPostResDto(post, commentsNum, imgUrl, tagNameList, ServiceUtil.getDataFormat(post.getCreatedAt()));
            searchPostResDtoList.add(searchPostResDto);
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.SEARCH_SUCCESS, searchPostResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

}

package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.SearchPostResDto;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.repository.CommentRepository;
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

    private final ServiceUtil serviceUtil;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostTagRepository postTagRepository;


    @Transactional
    public ResponseEntity<?> searchPost(String searchWord) {
        List<Post> postList = postRepository.findAllByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(searchWord, searchWord);
        List<SearchPostResDto> searchPostResDtoList = new ArrayList<>();

        System.out.println(searchWord);

        for (Post post : postList) {

            Long commentsNum = commentRepository.countByPost(post);

            List<String> imgUrlList = post.getImgUrl();
            String imgUrl = null;
            if(!imgUrlList.isEmpty()){
                imgUrl = imgUrlList.get(0);
            }

            List<PostTag> postTagList = postTagRepository.findAllByPost(post);
            List<String> tagNameList = new ArrayList<>();

            for (PostTag postTag : postTagList) {
                String tagName = postTag.getTag().getTagName();
                tagNameList.add(tagName);
            }

            SearchPostResDto searchPostResDto = new SearchPostResDto(post, commentsNum, imgUrl, tagNameList, serviceUtil.getDataFormatOfPost(post));
            searchPostResDtoList.add(searchPostResDto);
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.SEARCH_SUCCESS, searchPostResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

}

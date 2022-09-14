package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.dto.response.PostResDto;
import com.velog.backend.entity.Post;
import com.velog.backend.exception.SuccessMsg;
import com.velog.backend.repository.PostRepository;
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


    @Transactional
    public ResponseEntity<?> searchPost(String searchWord) {
        List<Post> postList = postRepository.findAll();
        List<PostResDto> postResDtoList = new ArrayList<>();

        for (Post post : postList) {

            if (post.getTitle().contains(searchWord)) {
                postResDtoList.add(PostResDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imgUrl(post.getImgUrl())
                        .build());
            } else if (post.getContent().contains(searchWord)) {
                postResDtoList.add(PostResDto.builder()
                        .postId(post.getPostId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imgUrl(post.getImgUrl())
                        .build());
            }
        }

        GlobalResDto<?> globalResDto = new GlobalResDto<>(HttpStatus.OK, SuccessMsg.SEARCH_SUCCESS, postResDtoList);

        return new ResponseEntity<>(globalResDto, HttpStatus.OK);
    }

}

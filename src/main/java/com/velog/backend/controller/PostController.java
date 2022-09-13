package com.velog.backend.controller;

import com.velog.backend.dto.request.PostReqDto;
import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("/auth/post")
    public ResponseEntity<?> createPost(@RequestBody @Valid PostReqDto postReqDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.createPost(postReqDto, userDetails);
    }

//    // 게시글 수정
//    @PutMapping("/auth/post/{postId}")
//    public ResponseEntity<?> updatePost(@PathVariable Long postId, @RequestBody PostReqDto postReqDto,@AuthenticationPrincipal UserDetailsImpl userDetails){
//        return postService.updatePost(postId,postReqDto,userDetails);
//    }

}

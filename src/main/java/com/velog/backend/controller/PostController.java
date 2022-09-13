package com.velog.backend.controller;

import com.velog.backend.dto.request.PostReqDto;
import com.velog.backend.image.S3ImageUploader;
import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final S3ImageUploader s3ImageUploader;
    private final PostService postService;

    // 게시글 생성
    @PostMapping("/auth/post")
    public ResponseEntity<?> createPost(@RequestBody @Valid PostReqDto postReqDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.createPost(postReqDto, userDetails);
    }


    @PostMapping("/auth/post/img")
    public String uploadImage(@RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        String imgUrl = null;
        if(!multipartFile.isEmpty()) {
            imgUrl = s3ImageUploader.uploadImage(multipartFile, "post");
        }
        return imgUrl;
    }

    // 게시글 수정
    @PutMapping("/auth/post/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,@RequestBody @Valid PostReqDto postReqDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.updatePost(postId, postReqDto, userDetails);
    }

    // 게시글 삭제
    @DeleteMapping("/auth/post/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return postService.deletePost(postId,userDetails);
    }
}

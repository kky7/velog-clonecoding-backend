package com.velog.backend.controller;

import com.velog.backend.image.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final S3ImageUploader s3ImageUploader;

    @PostMapping("/auth/post/img")
    public String uploadImage(@RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        String imgUrl = null;
        if(!multipartFile.isEmpty()) {
            imgUrl = s3ImageUploader.uploadImage(multipartFile, "post");
        }
        return imgUrl;
    }
}

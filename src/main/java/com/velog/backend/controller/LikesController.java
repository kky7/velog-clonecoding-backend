package com.velog.backend.controller;

import com.velog.backend.dto.response.LikesResDto;
import com.velog.backend.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/auth/likes")
    public ResponseEntity<?> likes(@RequestBody LikesResDto likesResDto) {
        return likesService.likes(likesResDto);
    }
}

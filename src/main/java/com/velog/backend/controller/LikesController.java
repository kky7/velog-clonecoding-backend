package com.velog.backend.controller;

import com.velog.backend.dto.response.LikesResDto;
import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LikesController {

    private final LikesService likesService;

    @PostMapping("/auth/likes")
    public ResponseEntity<?> likes(@RequestBody LikesResDto likesResDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likesService.getLikes(likesResDto, userDetails);
    }
}

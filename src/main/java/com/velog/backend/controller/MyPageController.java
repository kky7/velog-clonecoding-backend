package com.velog.backend.controller;

import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/member/mypage/{nickname}")
    public ResponseEntity<?> getAllPostByMember(@PathVariable String nickname) {
        return myPageService.getAllPostByMember(nickname);
    }

    @GetMapping("/auth/member/mypage/likes")
    public ResponseEntity<?> getAllPostByMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return myPageService.getMyLikePost(userDetails);
    }
}

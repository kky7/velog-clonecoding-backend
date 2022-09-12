package com.velog.backend.controller;

import com.velog.backend.dto.request.EmailReqDto;
import com.velog.backend.dto.request.LoginReqDto;
import com.velog.backend.dto.request.SignupReqDto;
import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 이메일 중복체크
    @PostMapping("/member/email")
    public ResponseEntity<?> emailCheck(@RequestBody EmailReqDto emailRequestDto) {
        return memberService.emailCheck(emailRequestDto);
    }

    // 회원 가입
    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupReqDto SignupRequestDto) {
        return memberService.signup(SignupRequestDto);
    }

    // 로그인
    @PostMapping("/member/login/normal")
    public ResponseEntity<?> login(@RequestBody @Valid LoginReqDto loginReqDto, HttpServletResponse response) {
        return memberService.login(loginReqDto, response);
    }

    // 로그아웃
    @PostMapping("/auth/member/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.logout(request, userDetails);
    }

    // reissue
    @PostMapping("/member/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return memberService.reissue(request,response);
    }
    
    // 프로필 사진 요청
    // reissue
    @PostMapping("/auth/member/profile-img")
    public ResponseEntity<?> getProfileUrl(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.getProfileUrl(userDetails);
    }
}

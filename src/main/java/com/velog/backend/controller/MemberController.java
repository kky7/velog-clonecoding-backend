package com.velog.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.velog.backend.dto.request.EmailReqDto;
import com.velog.backend.dto.request.LoginReqDto;
import com.velog.backend.dto.request.SignupReqDto;
import com.velog.backend.security.user.UserDetailsImpl;
import com.velog.backend.service.KakaoService;
import com.velog.backend.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final KakaoService kakaoService;

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
    
    // 프로필 정보 요청
    @GetMapping("/auth/member/profile-info")
    public ResponseEntity<?> getProfileInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.getProfileInfo(userDetails);
    }

    // 카카오 로그인
    @GetMapping("/member/login/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드 - 클라이언트가 카카오 서버로 부터 받아서 백으로 보내줌
        return kakaoService.kakaoLogin(code, response);
    }
}

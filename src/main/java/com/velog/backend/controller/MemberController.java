package com.velog.backend.controller;

import com.velog.backend.dto.request.EmailReqDto;
import com.velog.backend.dto.request.SignupReqDto;
import com.velog.backend.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}

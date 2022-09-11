package com.velog.backend.entity;
import com.velog.backend.dto.request.SignupReqDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // 일반회원가입은 이메일 지정, 카카오 로그인은 카카오 이메일
    @Column(nullable = false)
    private String email;

    // 일반회원가입은 회원가입시 지정, 카카오 로그인은 카카오 닉네임 사용
    @Column(nullable = false)
    private String nickname;

    // 일반회원가입은 회원가입시 지정, 카카오 로그인은 임의의 패스워드 만듦
    @Column(nullable = false)
    private String password;

    // 일반회원가입은 회원가입시 지정, 카카오 로그인은 초기값 null
    @Column
    private String introduction;

    // 일반회원가입시 null, 카카오 로그인은 카카오 아이디
    @Column
    private String kakaoId;

    public Member(SignupReqDto signupReqDto){
        this.email = signupReqDto.getEmail();
        this.nickname = signupReqDto.getNickname();
        this.password = signupReqDto.getPassword();
        this.introduction = signupReqDto.getIntroduction();
    }
}

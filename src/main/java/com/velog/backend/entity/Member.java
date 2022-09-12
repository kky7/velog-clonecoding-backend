package com.velog.backend.entity;
import com.velog.backend.dto.request.SignupReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // 일반회원가입은 이메일 필수 지정, 카카오 로그인은 회원이 선택했으면 기입 안했으면 null
    @Column
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

    @Column
    private String profileUrl;

    @Column
    private String velogTitle;

    public boolean validatePassword(PasswordEncoder passwordEncoder, String password) {
        return passwordEncoder.matches(password, this.password);
    }

    public Member(SignupReqDto signupReqDto, String velogTitle){
        this.email = signupReqDto.getEmail();
        this.nickname = signupReqDto.getNickname();
        this.password = signupReqDto.getPassword();
        this.introduction = signupReqDto.getIntroduction();
        this.velogTitle = velogTitle;
    }
}

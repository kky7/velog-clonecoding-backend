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

    @Column
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column
    private String introduction;

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

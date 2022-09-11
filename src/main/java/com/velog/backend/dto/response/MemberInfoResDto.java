package com.velog.backend.dto.response;

import com.velog.backend.dto.request.SignupReqDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoResDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String introduction;

    public MemberInfoResDto(Long memberId, SignupReqDto signupReqDto){
        this.memberId = memberId;
        this.email = signupReqDto.getEmail();
        this.nickname = signupReqDto.getNickname();
        this.introduction = signupReqDto.getIntroduction();
    }
}

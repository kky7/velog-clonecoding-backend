package com.velog.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInfoResDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String introduction;
    private String profileUrl;
    private String velogTitle;

    public MemberInfoResDto(Long memberId, String email, String nickname, String introduction, String profileUrl, String velogTitle){
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileUrl = profileUrl;
        this.velogTitle = velogTitle;
    }
}

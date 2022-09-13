package com.velog.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String profileUrl;

    public KakaoUserInfoDto(Long id, String nickname, String profileUrl){
        this.id = id;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
    }
}

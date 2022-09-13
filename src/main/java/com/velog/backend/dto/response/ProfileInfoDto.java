package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileInfoDto {
    private String imgUrl;
    private String nickname;
    private String introduction;

    public ProfileInfoDto(Member member){
        this.imgUrl = member.getProfileUrl();
        this.nickname = member.getNickname();
        this.introduction = member.getIntroduction();
    }
}

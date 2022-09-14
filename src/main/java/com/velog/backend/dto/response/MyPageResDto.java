package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyPageResDto {
    private String nickname;
    private String velogTitle;
    private String profileUrl;
    private String introduction;
    private List<String> tagList;
    private List<Long> numOfTagList;
    private List<MyPagePostResDto> postList;

    public MyPageResDto(Member member, List<String>tagList, List<Long> numOfTagList, List<MyPagePostResDto> postList){
        this.nickname = member.getNickname();
        this.velogTitle = member.getVelogTitle();
        this.profileUrl = member.getProfileUrl();
        this.introduction = member.getIntroduction();
        this.tagList = tagList;
        this.numOfTagList = numOfTagList;
        this.postList = postList;
    }
}

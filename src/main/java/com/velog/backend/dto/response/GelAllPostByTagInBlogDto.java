package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class GelAllPostByTagInBlogDto {
    private String nickname;
    private String profileUrl;
    private String introduction;
    private String velogTitle;
    private List<PostOfTagDto> postList;

    public GelAllPostByTagInBlogDto(Member member, List<PostOfTagDto> postList){
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfileUrl();
        this.introduction = member.getIntroduction();
        this.velogTitle = member.getVelogTitle();
        this.postList = postList;
    }
}

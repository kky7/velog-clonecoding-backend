package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class TagSearchPostsDto {
    private String nickname;
    private String profileUrl;
    private String introduction;
    private String velogTitle;
    private List<TagSearchPostDto> posts;

    public TagSearchPostsDto(Member member, List<TagSearchPostDto> postList){
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfileUrl();
        this.introduction = member.getIntroduction();
        this.velogTitle = member.getVelogTitle();
        this.posts = postList;
    }
}

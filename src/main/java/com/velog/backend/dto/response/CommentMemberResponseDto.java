package com.velog.backend.dto.response;

import com.velog.backend.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CommentMemberResponseDto {

    private Long memberId;
    private String nickname;
    private String profileUrl;


    public CommentMemberResponseDto(Member member) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.profileUrl = member.getProfileUrl();
    }
}

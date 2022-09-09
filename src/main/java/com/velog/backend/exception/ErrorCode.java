package com.velog.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // 회원가입, 로그인 관련 에러
    DUPLICATE_ID("DUPLICATE_ID", "중복된 아이디가 있습니다."),
    DUPLICATE_NICKNAME("DUPLICATE_NICKNAME", "중복된 닉네임이 있습니다."),
    PASSWORDS_NOT_MATCHED("PASSWORDS_NOT_MATCHED", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND", "사용자를 찾을 수 없습니다."),

    // 권한 요청 시 Access 토큰을 보내지 않은 경우
    INVALID_LOGIN("INVALID_LOGIN", "로그인이 필요합니다."),

    // reissue 또는 logout 시 Refresh 토큰을 보내지 않은 경우
    NEED_REFRESH_TOKEN("NEED_REFRESH_TOKEN","Refresh Token이 필요합니다."),
    NEED_ACCESS_TOKEN("NEED_ACCESS_TOKEN","Access Token이 필요합니다."),

    // 유효하지 않은 토큰
    INVALID_ACCESS_TOKEN("INVALID_ACCESS_TOKEN", "유효하지 않은 Access Token 입니다."),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN", "유효하지 않은 Refresh Token 입니다."),

    // 만료된 토큰
    EXPIRED_ACCESS_TOKEN("EXPIRED_ACCESS_TOKEN", "만료된 Access Token 입니다."),
    EXPIRED_REFRESH_TOKEN("EXPIRED_REFRESH_TOKEN", "만료된 Refresh Token 입니다."),

    // 포스팅, 코멘트 관련 에러
    POST_NOT_FOUND("POST_NOT_FOUND","해당 게시글이 존재하지 않습니다." ),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "해당 댓글이 존재하지 않습니다."),
    BAD_REQUEST("BAD_REQUEST","작성자만 수정 및 삭제를 할 수 있습니다."),

    // 마이페이지 관련 에러
    LIKE_POST_NOT_FOUND("LIKE_POST_NOT_FOUND","좋아요한 게시글이 없습니다."),
    MY_POST_NOT_FOUND("MY_POST_NOT_FOUND","내가 쓴 게시글이 없습니다."),

    // 좋아요 관련 에러
    LIKES_NOT_FOUND("LIKES_NOT_FOUND","좋아요를 하지 않은 게시글입니다."),
    DUPLICATE_LIKES("DUPLICATE_LIKES","이미 좋아요를 한 게시글입니다.");


    private final String code;
    private final String message;
}
package com.velog.backend.exception;

public interface ErrorMsg {
    // 회원가입
    String DUPLICATE_EMAIL = "중복된 이메일이 있습니다.";
    String INVALID_EMAIL = "잘못된 이메일 형식입니다.";
    String DUPLICATE_NICKNAME = "중복된 닉네임이 있습니다.";
    String PASSWORD_NOT_MATCHED = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";

    // 로그인
    String EMAIL_NOT_FOUND = "존재하지 않는 이메일 입니다.";
    String INVALID_PASSWORD = "비밀번호가 일치하지 않습니다.";

    // 권한 부여
    String INVALID_LOGIN = "로그인이 필요합니다.";
    String EXPIRED_ACCESS_TOKEN = "만료된 Access 토큰입니다.";
    String INVALID_ACCESS_TOKEN = "유효하지 않은 Access 토큰입니다.";
    String EXPIRED_REFRESH_TOKEN = "만료된 Refresh 토큰입니다.";
    String INVALID_REFRESH_TOKEN = "유효하지 않은 Refresh 토큰입니다.";

    // 토큰 필요
    String NEED_ACCESS_TOKEN = "Access 토큰이 필요합니다.";
    String NEED_REFRESH_TOKEN = "Refresh 토큰이 필요합니다.";

    // 사용자 찾을 수 없음
    String MEMBER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    String REFRESH_TOKEN_NOT_MATCHED = "Refresh 토큰이 일치하지 않습니다.";
}

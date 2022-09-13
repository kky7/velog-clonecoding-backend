package com.velog.backend.exception;

public interface SuccessMsg {
    String SIGNUP_OK = "회원가입을 진행하세요.";
    String SIGNUP_SUCCESS = "회원가입이 완료되었습니다.";
    String LOGIN_SUCCESS = "로그인 되었습니다.";
    String LOGOUT_SUCCESS = "로그아웃 되었습니다.";
    String REISSUE_ACCESS_TOKEN = "Access 토큰이 발급되었습니다.";
    String PROFILE_SUCCESS = "사용자 프로필 정보가 조회되었습니다.";
    String POST_SUCCESS = "게시글이 작성되었습니다.";
}

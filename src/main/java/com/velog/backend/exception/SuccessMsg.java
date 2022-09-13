package com.velog.backend.exception;

public interface SuccessMsg {
    String SIGNUP_OK = "회원가입을 진행하세요.";
    String SIGNUP_SUCCESS = "회원가입이 완료되었습니다.";
    String LOGIN_SUCCESS = "로그인 되었습니다.";
    String LOGOUT_SUCCESS = "로그아웃 되었습니다.";
    String REISSUE_ACCESS_TOKEN = "Access 토큰이 발급되었습니다.";
    String PROFILEURL_SUCCESS = "프로필 사진 URL이 조회되었습니다.";
    String CREATE_SUCCESS = "작성이 완료되었습니다.";
    String UPDATE_SUCCESS = "수정이 완료되었습니다.";
    String DELETE_SUCCESS = "삭제가 완료되었습니다.";
}

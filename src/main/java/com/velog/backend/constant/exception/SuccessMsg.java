package com.velog.backend.constant.exception;

public interface SuccessMsg {
    String SIGNUP_OK = "회원가입을 진행하세요.";
    String SIGNUP_SUCCESS = "회원가입이 완료되었습니다.";
    String LOGIN_SUCCESS = "로그인 되었습니다.";
    String LOGOUT_SUCCESS = "로그아웃 되었습니다.";
    String REISSUE_ACCESS_TOKEN = "Access 토큰이 발급되었습니다.";
    String PROFILE_SUCCESS = "사용자 프로필 정보가 조회되었습니다.";
    String CREATE_SUCCESS = "작성이 완료되었습니다.";
    String UPDATE_SUCCESS = "수정이 완료되었습니다.";
    String DELETE_SUCCESS = "삭제가 완료되었습니다.";

    String LIKE_SUCCESS = "좋아요가 완료되었습니다.";
    String LIKE_CANCEL = "좋아요가 취소되었습니다.";
    String TRANSFORM_SUCCESS = "이미지 URL 변환이 완료 되었습니다.";
    String SEARCH_SUCCESS = "검색 내역 조회가 완료되었습니다.";
}

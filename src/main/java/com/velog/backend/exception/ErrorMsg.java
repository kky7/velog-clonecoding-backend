package com.velog.backend.exception;

public interface ErrorMsg {
    String DUPLICATE_EMAIL = "중복된 이메일이 있습니다.";
    String INVALID_EMAIL = "잘못된 이메일 형식입니다.";
    String DUPLICATE_NICKNAME = "중복된 닉네임이 있습니다.";
    String PASSWORD_NOT_MATCHED = "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
}

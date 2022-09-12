package com.velog.backend.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginReqDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}

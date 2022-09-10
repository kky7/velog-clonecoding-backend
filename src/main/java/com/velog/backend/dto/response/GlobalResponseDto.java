package com.velog.backend.dto.response;

import com.velog.backend.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalResponseDto<T> {
    private boolean success;
    private T data;
    private ErrorCode error;

    public static <T> GlobalResponseDto<T> success(T data) {
        return new GlobalResponseDto<>(true, data, null);
    }
    public static <T> GlobalResponseDto<T> fail(ErrorCode code) {
        return new GlobalResponseDto<>(false, null, code);
    }
}

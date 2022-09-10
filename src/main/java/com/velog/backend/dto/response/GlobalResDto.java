package com.velog.backend.dto.response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class GlobalResDto<T> {
    private String status;
    private String msg;
    private T data;

    public GlobalResDto(HttpStatus httpStatus, String msg, T data){
        this.status = httpStatus.toString();
        this.msg = msg;
        this.data = data;
    }
}

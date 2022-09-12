package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceUtil {

    public ResponseEntity<?> dataNullResponse(HttpStatus httpStatus, String msg){
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus,msg);
        return new ResponseEntity<>(globalResDto,httpStatus);
    }

}

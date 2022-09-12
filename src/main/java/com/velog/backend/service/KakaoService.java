package com.velog.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Transactional
    public ResponseEntity<?> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

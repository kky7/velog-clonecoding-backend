package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class ServiceUtil {

    public ResponseEntity<?> dataNullResponse(HttpStatus httpStatus, String msg){
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus,msg);
        return new ResponseEntity<>(globalResDto,httpStatus);
    }
    
    public String CreateRandomString(){
        Random random = new Random();
        int length = random.nextInt(4)+3; // 3~6 자리

        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int choice = random.nextInt(3);
            switch(choice) {
                case 0:
                    randomString.append((char)((int)random.nextInt(25)+97)); // 소문자
                    break;
                case 1:
                    randomString.append((char)((int)random.nextInt(25)+65)); // 대문자
                    break;
                case 2:
                    randomString.append((char)((int)random.nextInt(10)+48)); // 숫자
                    break;
                default:
                    break;
            }
        }
        return randomString.toString();
    }

}

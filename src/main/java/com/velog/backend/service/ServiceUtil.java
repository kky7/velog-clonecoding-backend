package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Comment;
import com.velog.backend.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
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

    public String getDataFormatOfPost(Post post){
        LocalDate curDateTime = LocalDate.from(LocalDateTime.now());
        LocalDate postCreatedAt = LocalDate.from(post.getCreatedAt());
//        System.out.println(postCreatedAt.getYear()+"년"+" "+postCreatedAt.getMonth()+"월"+" "+postCreatedAt.getDayOfMonth()+"일");
        Period period = Period.between(postCreatedAt,curDateTime);
        String dateFormat = "";
        int days = (period.getDays());
        if(days < 1){
            LocalDateTime curTodayTime = LocalDateTime.now();
            LocalDateTime createdTime = post.getCreatedAt();
            Duration duration = Duration.between(createdTime,curTodayTime);
            double time = duration.getSeconds();
            double hour = time/3600;
            if(hour < 1){
                double minute = time/60;
                if(minute < 1){
                    dateFormat += "방금 전";
                }
                else{
                    dateFormat += (int)minute;
                    dateFormat += "분 전";
                }
            }else{
                dateFormat += (int)hour;
                dateFormat += "시간 전";
            }
        }else if( days < 8){
            dateFormat += days;
            dateFormat += "일 전";
        }else {
            dateFormat += postCreatedAt;
        }

        return dateFormat;
    }

    public String getDataFormatOfComment(Comment commment){
        LocalDate curDateTime = LocalDate.from(LocalDateTime.now());
        LocalDate postCreatedAt = LocalDate.from(commment.getCreatedAt());
        System.out.println(postCreatedAt);
        Period period = Period.between(postCreatedAt,curDateTime);
        String dateFormat = "";
        int days = (period.getDays());
        if(days < 1){
            LocalDateTime curTodayTime = LocalDateTime.now();
            LocalDateTime createdTime = commment.getCreatedAt();
            Duration duration = Duration.between(createdTime,curTodayTime);
            double time = duration.getSeconds();
            double hour = time/3600;
            if(hour < 1){
                double minute = time/60;
                if(minute < 1){
                    dateFormat += "방금 전";
                }
                else{
                    dateFormat += (int)minute;
                    dateFormat += "분 전";
                }
            }else{
                dateFormat += (int)hour;
                dateFormat += "시간 전";
            }
        }else if( days < 8){
            dateFormat += days;
            dateFormat += "일 전";
        }else {
            dateFormat += postCreatedAt;
        }

        return dateFormat;
    }

}

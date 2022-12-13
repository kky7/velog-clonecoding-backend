package com.velog.backend.service;

import com.velog.backend.constant.DateFormat;
import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Post;
import com.velog.backend.entity.PostTag;
import com.velog.backend.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceUtil {

    private final PostTagRepository postTagRepository;

    public static ResponseEntity<?> dataNullResponse(HttpStatus httpStatus, String msg){
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, msg);
        return new ResponseEntity<>(globalResDto, httpStatus);
    }

    public static String getDataFormat(LocalDateTime createdTime){
        LocalDateTime curTodayTime = LocalDateTime.now();
        LocalDate curDateTime = LocalDate.from(curTodayTime);
        LocalDate createdDateTime = LocalDate.from(createdTime);
        Period period = Period.between(createdDateTime, curDateTime);
        String dateFormat = "";
        int days = (period.getDays());
        if(days < 1){
            Duration duration = Duration.between(createdTime, curTodayTime);
            double time = duration.getSeconds();
            double hour = time/ DateFormat.secondsDuringHour;
            if(hour < 1){
                double minute = time/ DateFormat.secondsDuringMinute;
                if(minute < 1){
                    dateFormat += DateFormat.justNow;
                }
                else{
                    dateFormat += (int)minute;
                    dateFormat += DateFormat.minute;
                }
            }else{
                dateFormat += (int)hour;
                dateFormat += DateFormat.hour;
            }
        }else if(days < DateFormat.dayEndPoint){
            dateFormat += days;
            dateFormat += DateFormat.day;
        }else {
            dateFormat += createdDateTime;
        }

        return dateFormat;
    }

    public List<String> getTagNamesFromPostTag(Post post){
        List<PostTag> postTags = postTagRepository.findAllByPost(post);
        List<String> tagNames = new ArrayList<>();

        for(PostTag postTag : postTags){
            String tagName = postTag.getTag().getTagName();
            tagNames.add(tagName);
        }
        return tagNames;
    }

}

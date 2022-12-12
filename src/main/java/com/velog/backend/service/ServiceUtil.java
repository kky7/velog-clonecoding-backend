package com.velog.backend.service;

import com.velog.backend.dto.response.GlobalResDto;
import com.velog.backend.entity.Comment;
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

    public ResponseEntity<?> dataNullResponse(HttpStatus httpStatus, String msg){
        GlobalResDto<String> globalResDto = new GlobalResDto<>(httpStatus, msg);
        return new ResponseEntity<>(globalResDto, httpStatus);
    }

    public String getDataFormatOfPost(Post post){
        LocalDate curDateTime = LocalDate.from(LocalDateTime.now());
        LocalDate postCreatedAt = LocalDate.from(post.getCreatedAt());
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

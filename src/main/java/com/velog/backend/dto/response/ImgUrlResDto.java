package com.velog.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImgUrlResDto {
    private String imgUrl;

    public ImgUrlResDto(String imgUrl){
        this.imgUrl = imgUrl;
    }
}

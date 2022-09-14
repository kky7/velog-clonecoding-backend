package com.velog.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostReqDto {
    private String title;

    private String content;

    private List<String> imgUrl;

    private List<String> tag;
}

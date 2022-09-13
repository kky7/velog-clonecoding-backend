package com.velog.backend.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
public class PostReqDto {
    @NotBlank
    private String title;

    private String content;

    private List<String> imgUrl;

    private List<String> tag;
}

package com.velog.backend.controller;

import com.velog.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TagController {

    private final TagService tagService;

    @GetMapping("/velog/{nickname}")
    public ResponseEntity<?> tagSearchInblog(@PathVariable String nickname, @RequestParam(value = "tag") String tagName){
        return tagService.tagSearch(nickname,tagName);
    }
}

package com.velog.backend.controller;

import com.velog.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/velog")
    public ResponseEntity<?> searchPost(@RequestParam(value = "search") String searchWord) {
        return searchService.searchPost(searchWord);
    }
}

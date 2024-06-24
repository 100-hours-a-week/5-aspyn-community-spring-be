package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시물 전체 조회
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> retrieveAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /**
     * 특정 게시글 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable("id") int id) {
        return ResponseEntity.ok(postService.getPost(id));
    }
}

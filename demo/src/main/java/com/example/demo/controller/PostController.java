package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


    // 게시글 작성

    // 게시글 수정

    // 게시글 삭제
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Map<String, Object>>removePost(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        boolean isRemoved = postService.removePost(id);

        if (isRemoved) {
            response.put("status", "SUCCESS");
            response.put("message", "게시글이 삭제되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "ERROR");
            response.put("message", "댓글 삭제에 실패했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}

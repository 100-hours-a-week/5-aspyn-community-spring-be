package com.community.chalcak.post.controller;

import com.community.chalcak.post.dto.PostDto;
import com.community.chalcak.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 전체 조회
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> retrieveAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable("id") int id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // 게시글 작성
    @PostMapping("/edit")
    public ResponseEntity<Map<String, String>> newPost(@RequestPart PostDto postDto, @RequestPart MultipartFile image) throws IOException {
        Map<String, String> response = new HashMap<>();

        int newPostId = postService.newPost(postDto, image);

        if(newPostId > 0) {
            response.put("message", "게시글 작성이 완료되었습니다.");
            response.put("postId", String.valueOf(newPostId));
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "게시글 작성이 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 게시글 수정
    @Transactional
    @PatchMapping("/modify/{post}")
    public ResponseEntity<Map<String, String>> modifyPost(@RequestBody PostDto postDto) {
        Map<String, String> response = new HashMap<>();

        boolean isModified = postService.modifyPost(postDto);

        if (isModified) {
            response.put("status", "SUCCESS");
            response.put("message", "게시글 수정이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "게시글 수정에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

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

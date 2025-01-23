package com.community.chalcak.post.controller;

import com.community.chalcak.post.dto.PostRequestDto;
import com.community.chalcak.post.dto.PostGetDto;
import com.community.chalcak.post.dto.PostPageResponseDto;
import com.community.chalcak.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 전체 조회
    public ResponseEntity<Map<String, Object>> retrieveAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // 게시글 페이지네이션 조회
    @GetMapping("/list")
    public ResponseEntity<PostPageResponseDto> getPostList(@Valid PostGetDto postGetDto) {
        int page = postGetDto.getPage() - 1; // 페이지 번호 0부터 시작
        int size = postGetDto.getSize();
        PostService.PagedPosts pagedPosts = postService.getPagedPosts(page, size);

        // PostPageResponseDto 생성
        PostPageResponseDto response = PostPageResponseDto.builder()
                .data(pagedPosts.getPosts())
                .pageInfo(PostPageResponseDto.PageInfo.builder()
                        .page(page)
                        .size(size)
                        .totalElements(pagedPosts.getTotalElements())
                        .totalPages(pagedPosts.getTotalPages())
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    // 특정 게시글 조회 (게시글 + 댓글)
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable("id") int id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // 특정 게시글만 조회 (게시글 only)
    @GetMapping("/{id}only")
    public ResponseEntity<Map<String, Object>> getPostOnly(@PathVariable("id") int id) {
        return ResponseEntity.ok(postService.getPostOnly(id));
    }

    // 게시글 작성
    @PostMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<Map<String, String>> newPost(
            @RequestPart(value = "file") MultipartFile image, @RequestPart(value = "request") PostRequestDto postRequestDto) throws IOException {
        Map<String, String> response = new HashMap<>();

        int newPostId = postService.newPost(postRequestDto, image);

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
    @PatchMapping(value = "/{post}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<Map<String, String>> modifyPost(@RequestPart(value = "request") PostRequestDto postRequestDto) {
        Map<String, String> response = new HashMap<>();

        System.out.println(postRequestDto);

        boolean isModified = postService.modifyPost(postRequestDto);

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
    @DeleteMapping("/{id}")
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

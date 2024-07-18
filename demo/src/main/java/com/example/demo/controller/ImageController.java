package com.example.demo.controller;

import com.example.demo.dto.ImageDto;
import com.example.demo.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    // 프로필 이미지 업로드
    @PostMapping("/profile/upload")
    public ResponseEntity<String> uploadProfileImage(@RequestParam("userId") int userId, @RequestParam("file") MultipartFile file) throws IOException {
        imageService.uploadProfileImage(userId, file);
        return new ResponseEntity<>("프로필 이미지 업로드가 완료되었습니다.", HttpStatus.OK);
    }

    // 프로필 이미지 불러오기
    @GetMapping("/profile/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable int userId) throws IOException {
        ImageDto imageDto = imageService.getProfileImage(userId);
        Path path = Paths.get(imageDto.getPath());
        byte[] data = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageDto.getName() +"\"")
                .body(data);
    }

    // 게시글 이미지 업로드
    @PostMapping("/post/upload")
    public ResponseEntity<String> uploadPostImage(@RequestParam("postId") int postId, @RequestParam("file")MultipartFile file) throws IOException {
        imageService.uploadPostImage(postId, file);
        return new ResponseEntity<>("게시글 이미지가 업로드 되었습니다.", HttpStatus.OK);
    }

    // 게시글 이미지 불러오기
    @GetMapping("/post/{postId}")
    public ResponseEntity<byte[]>getPostImage(@PathVariable int postId) throws IOException {
        ImageDto imageDto = imageService.getPostImage(postId);
        Path path = Paths.get(imageDto.getPath());
        byte[] date = Files.readAllBytes(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageDto.getName() + "\"")
                .body(date);
    }


}

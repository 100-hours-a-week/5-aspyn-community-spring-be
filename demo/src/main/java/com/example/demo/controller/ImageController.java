package com.example.demo.controller;

import com.example.demo.dto.ImageDto;
import com.example.demo.service.ImageService;
import com.example.demo.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final S3Service s3Service;

    // 회원가입 시 프로필 이미지 업로드
    @PostMapping("/profile/upload")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestPart("email") String email,
            @RequestPart("profileImage") MultipartFile profileImage) {
        Map<String, String> response = new HashMap<>();
        try {
            if (profileImage == null || profileImage.isEmpty()) {
                response.put("message", "프로필 이미지가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }

            String imageUrl = s3Service.upload(profileImage, "profile/" + email);
            imageService.updateUserProfileImage(email, imageUrl);

            response.put("status", "SUCCESS");
            response.put("message", "프로필 이미지 업로드가 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "프로필 이미지 업로드 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 프로필 이미지 불러오기
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, String>> getProfileImage(@PathVariable int userId) {
        Map<String, String> response = new HashMap<>();
        try{
            String imageUrl = imageService.getUserProfileImage(userId);
            response.put("status", "SUCCESS");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 게시글 이미지 업로드
    @PostMapping("/post/upload")
    public ResponseEntity<String> uploadPostImage(@RequestParam("postId") int postId, @RequestParam("file")MultipartFile file) throws IOException {
//        imageService.uploadPostImage(postId, file);
        return new ResponseEntity<>("게시글 이미지가 업로드 되었습니다.", HttpStatus.OK);
    }

    // 게시글 이미지 불러오기
//    @GetMapping("/post/{postId}")
//    public ResponseEntity<byte[]>getPostImage(@PathVariable int postId) throws IOException {
//        ImageDto imageDto = imageService.getPostImage(postId);
//        Path path = Paths.get(imageDto.getPath());
//        byte[] date = Files.readAllBytes(path);
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageDto.getName() + "\"")
//                .body(date);
//    }


}

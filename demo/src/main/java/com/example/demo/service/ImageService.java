package com.example.demo.service;

import com.example.demo.dao.ImageDao;
import com.example.demo.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageDao imageDao;

    // 프로필 이미지 업로드
    public void uploadProfileImage(int userId, MultipartFile file) throws IOException {
        String uploadDir = "../../../resources/static/image/profile";
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        Files.write(filepath, file.getBytes());

        ImageDto imageDto = new ImageDto();
        imageDto.setUserId(userId);
        imageDto.setName(file.getOriginalFilename());
        imageDto.setPath(filepath.toString());

        imageDao.profileImageSave(imageDto);
    }

    // 프로필 이미지 불러오기
    public ImageDto getProfileImage(int userId) {
        return imageDao.findProfileImage(userId);
    }

    // 게시글 이미지 업로드
    public void uploadPostImage(int postId, MultipartFile file) throws IOException {
        String uploadDir = "../../../resources/static/image/post";
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(uploadDir, filename);
        Files.write(filepath, file.getBytes());

        ImageDto imageDto = new ImageDto();
        imageDto.setPostId(postId);
        imageDto.setName(file.getOriginalFilename());
        imageDto.setPath(filepath.toString());

        imageDao.postImageSave(imageDto);
    }

    // 게시글 이미지 불러오기
    public ImageDto getPostImage(int postId) {
        return imageDao.findPostImage(postId);
    }
}

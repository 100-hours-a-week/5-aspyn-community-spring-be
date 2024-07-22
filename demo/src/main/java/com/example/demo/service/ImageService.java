package com.example.demo.service;

import com.example.demo.dao.ImageDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageDao imageDao;

    // 사용자 프로필 이미지 URL 업데이트 로직
    public void updateUserProfileImage(String email, String imageUrl) {
        imageDao.updateUserProfileImage(email, imageUrl);
    }

    // 이미지 URL 조회
    public String getUserProfileImage(int userId) {
        return imageDao.getUserProfileImage(userId);
    }

}

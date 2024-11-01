package com.community.chalcak.image.service;

import com.community.chalcak.image.dao.ImageDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageDao imageDao;

    // 사용자 프로필 이미지 URL 업데이트 로직
    public void updateUserProfileImage(long userId, String imageUrl) {
        imageDao.updateUserProfileImage(userId, imageUrl);
    }

    // 이미지 URL 조회
    public String getUserProfileImage(long userId) {
        return imageDao.getUserProfileImage(userId);
    }

}

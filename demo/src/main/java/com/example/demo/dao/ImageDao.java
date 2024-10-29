package com.example.demo.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageDao {

    private final JdbcTemplate jdbcTemplate;

    // 회원가입 시 프로필 이미지 등록
    public void updateUserProfileImage(String email, String imageUrl) {
        String sql = "UPDATE user SET profile_image_url = ? WHERE email = ?";
        jdbcTemplate.update(sql, imageUrl, email);
    }

    // userId로 유저의 프로필 이미지 URL 조회
    public String getUserProfileImage(long userId) {
        String sql = "SELECT profile_image_url FROM user WHERE user_num = ?";
        return jdbcTemplate.queryForObject(sql,new Object[]{userId}, String.class);
    }
}

package com.community.chalcak.image.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageDao {

    private final JdbcTemplate jdbcTemplate;

    // 프로필 이미지 등록
    public void updateUserProfileImage(long userId, String imageUrl) {
        String sql = "UPDATE user SET profile_url = ? WHERE id = ?";
        jdbcTemplate.update(sql, imageUrl, userId);
    }

    // userId로 유저의 프로필 이미지 URL 조회
    public String getUserProfileImage(long userId) {
        String sql = "SELECT profile_url FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,new Object[]{userId}, String.class);
    }
}

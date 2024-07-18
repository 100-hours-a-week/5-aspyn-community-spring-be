package com.example.demo.dao;

import com.example.demo.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class ImageDao {
    private final JdbcTemplate jdbcTemplate;

    // 프로필 이미지 저장
    public void profileImageSave(ImageDto imageDto) {
        String sql = "INSERT INTO profile_images (user_num, name, path) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, imageDto.getUserId(),imageDto.getName(), imageDto.getPath());
    }

    // 프로필 이미지 불러오기
    public ImageDto findProfileImage(int user_num) {
        String sql = "SELECT * FROM profile_images WHERE user_num = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{user_num}, new RowMapper<ImageDto>() {
            @Override
            public ImageDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                ImageDto image = new ImageDto();
                image.setId(rs.getLong("id"));
                image.setUserId(rs.getInt("user_num"));
                image.setName(rs.getString("name"));
                image.setPath(rs.getString("path"));
                return image;
            }
        });
    }

    //게시글 이미지 저장
    public void postImageSave(ImageDto imageDto) {
        String sql = "INSERT INTO post_images (post_id, name, path) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, imageDto.getPostId(),imageDto.getName(), imageDto.getPath());
    }

    //게시글 이미지 불러오기
    public ImageDto findPostImage(int postId) {
        String sql = "SELECT * FROM post_images WHERE post_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{postId}, new RowMapper<ImageDto>() {
            @Override
            public ImageDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                ImageDto image = new ImageDto();
                image.setId(rs.getLong("id"));
                image.setPostId(rs.getInt("post_id"));
                image.setName(rs.getString("name"));
                image.setPath(rs.getString("path"));
                return image;
            }
        });
    }


}

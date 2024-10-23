package com.example.demo.dao;

import com.example.demo.dto.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 회원가입
    public boolean insertUser(User user) {
        String sql = "INSERT INTO user (email, password, nickname, profile_url) VALUES (?, ?, ?, ?)";
        int result = jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getNickname(), user.getProfileUrl());

        // result 값이 0보다 크면(업데이트된 행이 0 초과이면) true 반환
        return result > 0;
    }

    // 로그인 확인
    public User checkLogin(User user) {
        String sql = "SELECT id, email, password, nickname, profile_url, deleted_at FROM user WHERE email = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, (rs, rowNum) -> {
                User dbUser = new User(); // db에서 확인한 로그인 유저 정보
                dbUser.setId(rs.getInt("id"));
                dbUser.setEmail(rs.getString("email"));
                dbUser.setPassword(rs.getString("password"));
                dbUser.setNickname(rs.getString("nickname"));
                dbUser.setProfileUrl(rs.getString("profileUrl"));
                dbUser.setDeletedAt(rs.getDate("deletedAt"));
                return dbUser;
            });
        } catch (EmptyResultDataAccessException e) {
            // 로그인 하려는 이메일이 없는 경우
            return null;
        }
    }

    // 비밀번호 수정
    public boolean modifyPassword(User user) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";

        int result = jdbcTemplate.update(sql, user.getPassword(), user.getId());
        return result > 0 ;
    }

    // 닉네임 수정
    public boolean modifyNickname(User user) {
        String sql = "UPDATE user SET nickname = ? WHERE id = ?";

        int result = jdbcTemplate.update(sql, user.getNickname(),user.getId());
        return result > 0 ;
    }

    // 닉네임 중복 확인
    public boolean existByNickname (String nickname) {
        String sql = "SELECT nickname FROM user WHERE nickname = ? and deleted_at IS null";

        // 중복 닉네임이 존재하는지 확인
        List<String> results = jdbcTemplate.query(
                sql,
                new Object[]{nickname},
                (rs, rowNum) -> rs.getString("nickname")
        );

        // 결과가 비어있지 않으면 true(중복 있음), 비어있으면 false 반환
        return !results.isEmpty();
    }

    // 이메일 중복 확인
    public boolean checkEmail (User user) {
        // 탈퇴한 이메일 포함하여 중복 확인
        String sql = "SELECT count(*) FROM user WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, Integer.class);
        // 이메일 중복일 시 true
        return count != null && count > 0;
    }

    // 유저 정보 조회
    public Map<String, Object> loginUser (int id) {
        String sql = "SELECT id, email, nickname FROM user WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    // 회원탈퇴
    @Transactional
    public Map<String, Object> leaveUser (User user) {
        String sql1 = "UPDATE user SET deleted_at = now() WHERE id = ?";
        String sql2 = "UPDATE post SET deleted_at = now() WHERE id = ? AND deleted_at IS null";
        String sql3 = "UPDATE comments SET deleted_at = now() WHERE id = ? AND deleted_at IS null";

        Map<String, Object> result = new HashMap<>();

        try {
            // 유저 상태 업데이트
            int result1 = jdbcTemplate.update(sql1, user.getId());
            // 게시글 상태 업데이트
            int result2 = jdbcTemplate.update(sql2, user.getId());
            // 댓글 상태 업데이트
            int result3 = jdbcTemplate.update(sql3, user.getId());

            result.put("status", "SUCCESS");
            result.put("message", "회원탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "회원탈퇴 처리 중 오류가 발생했습니다.");
            result.put("error", e.getMessage());
        }

        return result;
    }

}

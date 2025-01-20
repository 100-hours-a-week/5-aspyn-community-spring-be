package com.community.chalcak.user.dao;

import com.community.chalcak.user.entity.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    // 유저 정보 조회(로그인)
    public User findByUsername(String username) {
        String sql = "SELECT id, email, password, nickname, profile_url, deleted_at FROM `user` WHERE email = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, (rs, rowNum) -> {
                User dbUser = new User(); // db에서 확인한 로그인 유저 정보
                dbUser.setId(rs.getInt("id"));
                dbUser.setEmail(rs.getString("email"));
                dbUser.setPassword(rs.getString("password"));
                dbUser.setNickname(rs.getString("nickname"));
                dbUser.setProfileUrl(rs.getString("profile_url"));
                dbUser.setDeletedAt(rs.getTimestamp("deleted_at"));

                System.out.println("데이터베이스 결과 조회 : " + dbUser);
                return dbUser;
            });
        } catch (EmptyResultDataAccessException e) {
            // 로그인 하려는 이메일이 없는 경우
            return null;
        }
    }

    // 비밀번호 수정
    public boolean modifyPassword(long userId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";

        int result = jdbcTemplate.update(sql, newPassword, userId);
        return result > 0 ;
    }

    // 프로필 url 조회
    public String getProfileUrl(long id) {
        String sql = "SELECT profile_url FROM user WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,String.class, id);
    }

    // 닉네임, 프로필 이미지 변경
    public boolean modifyInfo(User user) {
        StringBuilder sql = new StringBuilder("UPDATE user SET ");
        Map<String, Object> updates = new LinkedHashMap<>();

        if(user.getNickname() != null) {
            sql.append("nickname = ?, ");
            updates.put("nickname", user.getNickname());
        }

        if(user.getProfileUrl() != null) {
            sql.append("profile_url = ?, ");
            updates.put("profile_url", user.getProfileUrl());
        }

        sql.delete(sql.length()-2, sql.length()); // 쿼리 끝에 쉼표 제거
        sql.append(" WHERE id = ?");
        updates.put("id", user.getId());

        Object[] params = updates.values().toArray();

        int result = jdbcTemplate.update(sql.toString(), params);
        return result > 0;
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
    public boolean checkEmail (String email) {
        // 탈퇴한 이메일 포함하여 중복 확인
        String sql = "SELECT count(*) FROM user WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        // 이메일 중복일 시 true
        return count != null && count > 0;
    }

    // 유저 정보 조회
    public Map<String, Object> getUserInfo (long id) {
        String sql = "SELECT id, email, nickname, profile_url FROM user WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

    // 회원탈퇴
    public Map<String, Object> leaveUser (Long userId) {
        String sql1 = "UPDATE user SET deleted_at = now(), profile_url = null WHERE id = ?";

        Map<String, Object> result = new HashMap<>();

        try {
            // 유저 상태 업데이트
            int result1 = jdbcTemplate.update(sql1, userId);

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

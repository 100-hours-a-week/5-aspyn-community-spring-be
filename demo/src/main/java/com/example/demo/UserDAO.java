package com.example.demo;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 회원가입
    public boolean insertUser(User user) {
        String sql = "INSERT INTO user (email, password, nickname) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getNickname());

        // result 값이 0보다 크면(업데이트된 행이 0 초과이면) true 반환
        return result > 0;
    }

    // 로그인 확인
    public User checkLogin(User user) {
        String sql = "SELECT user_num, email, password, nickname, leave FROM user WHERE email = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, (rs, rowNum) -> {
                User dbUser = new User(); // db에서 확인한 로그인 유저 정보
                dbUser.setUser_num((rs.getInt("user_num")));
                dbUser.setEmail(rs.getString("Email"));
                dbUser.setPassword(rs.getString("Password"));
                dbUser.setNickname(rs.getString("Nickname"));
                dbUser.setLeave(rs.getString("Leave"));
                return dbUser;
            });
        } catch (EmptyResultDataAccessException e) {
            // 로그인 하려는 이메일이 없는 경우
            return null;
        }
    }
}

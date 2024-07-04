package com.example.demo.dao;

import com.example.demo.dto.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        String sql = "INSERT INTO user (email, password, nickname) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sql, user.getEmail(), user.getPassword(), user.getNickname());

        // result 값이 0보다 크면(업데이트된 행이 0 초과이면) true 반환
        return result > 0;
    }

    // 로그인 확인
    public User checkLogin(User user) {
        String sql = "SELECT user_num, email, password, nickname, `leave` FROM user WHERE email = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{user.getEmail()}, (rs, rowNum) -> {
                User dbUser = new User(); // db에서 확인한 로그인 유저 정보
                dbUser.setUser_num(rs.getInt("user_num"));
                dbUser.setEmail(rs.getString("email"));
                dbUser.setPassword(rs.getString("password"));
                dbUser.setNickname(rs.getString("nickname"));
                dbUser.setLeave(rs.getString("leave"));
                return dbUser;
            });
        } catch (EmptyResultDataAccessException e) {
            // 로그인 하려는 이메일이 없는 경우
            return null;
        }
    }

    // 비밀번호 수정
    public boolean modifyPassword(User user) {
        String sql = "UPDATE user SET password = ? WHERE user_num = ?";

        int result = jdbcTemplate.update(sql, user.getPassword(), user.getUser_num());
        return result > 0 ;
    }

    // 닉네임 수정
    public boolean modifyNickname(User user) {
        String sql = "UPDATE user SET nickname = ? WHERE user_num = ?";

        int result = jdbcTemplate.update(sql, user.getNickname(),user.getUser_num());
        return result > 0 ;
    }

    // 이메일 중복 확인


    // 닉네임 중복 확인
    public boolean existByNickname (String nickname) {
        String sql = "SELECT nickname FROM user WHERE nickname = ? and `leave` = 'N'";

        // 중복 닉네임이 존재하는지 확인
        List<String> results = jdbcTemplate.query(
                sql,
                new Object[]{nickname},
                (rs, rowNum) -> rs.getString("nickname")
        );

        // 결과가 비어있지 않으면 true(중복 있음), 비어있으면 false 반환
        return !results.isEmpty();
    }

    // 유저 정보 조회
    public Map<String, Object> loginUser (int id) {
        String sql = "SELECT user_num, email, nickname FROM user WHERE user_num = ?";
        return jdbcTemplate.queryForMap(sql, id);
    }

}

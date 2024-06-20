package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;

public class JoinService {
    @Autowired
    private UserDAO userDAO;

    // 코드 이해 필요 - 아직 이해 못함
    public boolean registerUser(UserDTO userDTO) throws SQLException {

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(userDTO.getId());
        user.setPassword(userDTO.getPassword());
        user.setNickname(userDTO.getNickname());

        return userDAO.insertUser(user);
    }
}

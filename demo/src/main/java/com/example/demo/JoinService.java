package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class JoinService {
    private UserDAO userDAO;

    @Autowired
    public JoinService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean registerUser(UserDTO userDTO) throws SQLException {

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setNickname(userDTO.getNickname());

        // 데이터베이스 저장 처리 후 결과 반환
        return userDAO.insertUser(user);
    }
}

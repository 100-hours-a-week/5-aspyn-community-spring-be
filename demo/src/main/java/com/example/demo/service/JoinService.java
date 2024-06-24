package com.example.demo.service;

import com.example.demo.dto.User;
import com.example.demo.dao.UserDao;
import com.example.demo.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class JoinService {
    private UserDao userDAO;

    @Autowired
    public JoinService(UserDao userDAO) {
        this.userDAO = userDAO;
    }

    public boolean registerUser(UserDto userDTO) throws SQLException {

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setNickname(userDTO.getNickname());

        // 데이터베이스 저장 처리 후 결과 반환
        return userDAO.insertUser(user);
    }
}

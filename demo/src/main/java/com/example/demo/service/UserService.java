package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;
import com.example.demo.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDAO;

    /**
     * 회원가입
     *
     * @param userDTO
     * @return boolean
     * */
    @Transactional
    public boolean registerUser(UserDto userDTO) throws SQLException {

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setNickname(userDTO.getNickname());

        // 데이터베이스 저장 처리 후 결과 반환
        return userDAO.insertUser(user);
    }

    /**
     * 로그인
     *
     * @param userDTO
     * @return Map<String, Object>
     * */
    public Map<String, Object> login(UserDto userDTO) {
        Map<String, Object> result = new HashMap<>();

        // 로그인 시도 유저 정보
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        // 로그인 시도 이메일과 디비 정보 대조하여 데이터 가져옴
        User dbUser = userDAO.checkLogin(user);

        if (dbUser == null) {
            result.put("status", "ERROR");
            result.put("message", "존재하지 않는 이메일입니다.");
        } else if (dbUser.getLeave().equals("Y")){
            result.put("status", "ERROR");
            result.put("message", "탈퇴한 계정입니다.");
        } else if (!dbUser.getPassword().equals(user.getPassword())){
            result.put("status", "ERROR");
            result.put("message", "비밀번호가 일치하지 않습니다.");
        } else {
            // 로그인 성공
            result.put("status", "SUCCESS");
            result.put("user", dbUser);
        }
        return result;
    }

    // 비밀번호 수정
    public boolean changePassword(UserDto userDto) {

        User user = new User();
        user.setUser_num(userDto.getUser_num());
        user.setPassword(userDto.getPassword());

        return userDAO.changePassword(user);
    }

    // 닉네임 수정
}

package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;
import com.example.demo.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // 로그인 시도 이메일과 db 정보 대조하여 데이터 가져옴
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

    // 로그아웃
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 세션 무효화
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        result.put("status", "SUCCESS");
        result.put("message", "로그아웃이 성공적으로 처리되었습니다.");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 비밀번호 수정
    public boolean modifyPassword(UserDto userDto) {

        User user = new User();
        user.setUser_num(userDto.getUser_num());
        user.setPassword(userDto.getPassword());

        return userDAO.modifyPassword(user);
    }

    // 닉네임 수정
    public boolean modifyNickname(UserDto userDto) {

        User user = new User();
        user.setUser_num(userDto.getUser_num());
        user.setNickname(userDto.getNickname());

        return userDAO.modifyNickname(user);
    }

    // 닉네임 중복 확인
    public boolean checkNickname(String nickname) {
        return userDAO.existByNickname(nickname);
    }

    // 유저 정보 조회
    public ResponseEntity<Map<String, Object>> loginUser(int id) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = userDAO.loginUser(id);

        if (result == null) {
            response.put("message", "유저를 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.put("id", result.get("user_num"));
            response.put("email", result.get("email"));
            response.put("nickname", result.get("nickname"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // 회원탈퇴
    public ResponseEntity<Map<String, Object>> leaveUser(UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setUser_num(userDto.getUser_num());

        Map<String,Object> result = userDAO.leaveUser(user);

        if ("SUCCESS".equals(result.get("status"))){
            // 세션 무효화
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 쿠키 삭제
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/"); // 필요에 따라 경로를 수정합니다.
                    response.addCookie(cookie);
                }
            }

            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

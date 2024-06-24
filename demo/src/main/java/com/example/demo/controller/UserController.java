package com.example.demo.controller;

import com.example.demo.dto.User;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // @PostMapping 이랑 같은 기능
    // @RequestMapping(method = RequestMethod.POST, path = "/user/join")
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> join(@RequestBody UserDto userDTO) throws SQLException {
        Map<String,String> response = new HashMap<>();
        try {
            // 회원가입
            if (userDTO == null) {
                response.put("message", "가입할 회원 정보가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 신규 회원 정보 디비에 생성
            boolean isJoined = userService.registerUser(userDTO);

            if (isJoined) {
                response.put("message", "회원가입이 완료되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "회원가입에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 메세지 콘솔 출력
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto userDTO) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> loginResult = userService.login(userDTO);

        if ("SUCCESS".equals(loginResult.get("status"))) {
            User user = (User) loginResult.get("user");
            response.put("message", "로그인이 완료되었습니다.");
            response.put("status", "SUCCESS");
            response.put("userId", String.valueOf(user.getUser_num()));
            return ResponseEntity.ok(response);
        } else {
            response.put("message", (String) loginResult.get("message"));
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}
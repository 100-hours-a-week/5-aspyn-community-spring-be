package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final JoinService joinService;
    private final LoginService loginService;

    @Autowired
    public UserController(JoinService joinService, LoginService loginService) {
        this.joinService = joinService;
        this.loginService = loginService;
    }

    // @PostMapping 이랑 같은 기능
    // @RequestMapping(method = RequestMethod.POST, path = "/user/join")
    @PostMapping("/join")
    public ResponseEntity<Map<String, String>> join(@RequestBody UserDTO userDTO) throws SQLException {
        Map<String,String> response = new HashMap<>();
        try {
            // 회원가입
            if (userDTO == null) {
                response.put("message", "가입할 회원 정보가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 신규 회원 정보 디비에 생성
            boolean isJoined = joinService.registerUser(userDTO);

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
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> loginResult = loginService.login(userDTO);

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
package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/userinfo")
    public ResponseEntity<Map<String, String>> getUserInfo(HttpSession session) {

        // 세션에서 유저 정보 가져오기
        String user_num = session.getAttribute("user_num").toString();

        if (user_num != null) {
            Map<String, String> response = new HashMap<>();
            response.put("user_num", user_num);

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "로그인이 필요합니다.");
            response.put("status", "ERROR");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

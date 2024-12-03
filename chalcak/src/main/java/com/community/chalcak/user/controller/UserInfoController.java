package com.community.chalcak.user.controller;

import com.community.chalcak.user.dto.CustomUserDetails;
import com.community.chalcak.user.dto.UserResponseDto;
import com.community.chalcak.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserInfoController {

    private final UserService userService;

    public UserInfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String userUsingSession() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return "세션 현재 사용자 아이디 : " + name;
    }

    @GetMapping("/api/userinfo")
    public ResponseEntity<Map<String, String>> getUserInfo() {

        // Authentication 객체에서 Principal(유저 정보) 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 유저 정보에서 userId와 username 가져오기
        Long userId = userDetails.getUserId();

        System.out.println("jwt 인증 후 컨텍스트홀더에서 가져온 유저 id: " + userId);

        if (userId != null) {
            UserResponseDto userDto = userService.getUserInfo(userId);

            Map<String, String> response = new HashMap<>();
            response.put("user_id", String.valueOf(userDto.getId()));
            response.put("email", userDto.getEmail());
            response.put("nickname", userDto.getNickname());
            response.put("profile_url", userDto.getProfileUrl());

            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "로그인이 필요합니다.");
            response.put("status", "ERROR");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}

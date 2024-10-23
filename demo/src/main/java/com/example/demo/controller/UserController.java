package com.example.demo.controller;

import com.example.demo.dto.User;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    // @PostMapping 이랑 같은 기능
    // @RequestMapping(method = RequestMethod.POST, path = "api/user/join")
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
                response.put("status", "SUCCESS");
                response.put("message", "회원가입이 완료되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "ERROR");
                response.put("message", "회원가입에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 메세지 콘솔 출력
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 이메일 중복 확인
    @PostMapping("/isExist")
    public ResponseEntity<Boolean> checkEmail(@RequestBody UserDto userDto) {
        return userService.checkEmail(userDto);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto userDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> loginResult = userService.login(userDTO);

        if ("SUCCESS".equals(loginResult.get("status"))) {
            User user = (User) loginResult.get("user");

            // 세션에 유저 정보 저장
            session.setAttribute("user_id", user.getId());

            response.put("message", "로그인이 완료되었습니다.");
            response.put("status", "SUCCESS");
            response.put("userId", String.valueOf(user.getId()));

            // 쿠키에 유저 ID 저장
            ResponseCookie responseCookie = ResponseCookie.from("user_num", String.valueOf(user.getId()))
                    .path("/")
                    .httpOnly(true)
                    .maxAge(1800)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                    .body(response);
        } else {
            response.put("message", (String) loginResult.get("message"));
            response.put("status", "ERROR");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletRespzonse response) {
        return userService.logout(request);
    }

    // 비밀번호 수정
    @PatchMapping("/modifyPassword")
    public ResponseEntity<Boolean> modifyPassword (@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.modifyPassword(userDto));
    }

    // 닉네임 수정
    @PatchMapping("/modifyNickname")
    public ResponseEntity<Boolean> modifyNickname (@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.modifyNickname(userDto));
    }

    // 닉네임 중복 확인
    @GetMapping("/isExist/{nickname}")
    public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }

    // 유저 정보 조회
    @GetMapping("/loginUser/{id}")
    public ResponseEntity<Map<String, Object>> loginUser(@PathVariable int id) {
        return userService.loginUser(id);
    }

    // 회원탈퇴
    @DeleteMapping("/leaveUser")
    public ResponseEntity<Map<String, Object>> leaveUser(@RequestBody UserDto userDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.leaveUser(userDto, request, response);
    }


}
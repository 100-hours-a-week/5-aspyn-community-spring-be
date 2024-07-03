package com.example.demo.controller;

import com.example.demo.dto.User;
import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
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

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto userDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> loginResult = userService.login(userDTO);

        if ("SUCCESS".equals(loginResult.get("status"))) {
            User user = (User) loginResult.get("user");

            // 세션에 유저 정보 저장
            session.setAttribute("user_num", user.getUser_num());

            response.put("message", "로그인이 완료되었습니다.");
            response.put("status", "SUCCESS");
            response.put("userId", String.valueOf(user.getUser_num()));

            // 쿠키에 유저 ID 저장
            ResponseCookie responseCookie = ResponseCookie.from("user_num", String.valueOf(user.getUser_num()))
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

    // 비밀번호 수정
    @PatchMapping("/password")
    public boolean changePassword (@RequestBody UserDto userDto) {
        return userService.changePassword(userDto);
    }

    // 닉네임 수정

//    @GetMapping("/test")
//    public ResponseEntity<Void> test() {
//        ResponseCookie responseCookie = ResponseCookie.from("user_num", String.valueOf(4))
//                .path("/").httpOnly(true).maxAge(1800).build();
//
//        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).build();
//    }
//
//    @GetMapping("/test2")
//    public ResponseEntity<Void> test2( HttpSession httpSession) {
//       return ResponseEntity.ok().build();
//    }

}
package com.community.chalcak.user.controller;

import com.community.chalcak.user.entity.User;
import com.community.chalcak.user.dto.UserRequestDto;
import com.community.chalcak.image.service.S3Service;
import com.community.chalcak.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    //회원가입
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> join(
            @RequestPart(value = "request") UserRequestDto userRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile profileImage
    ) throws IOException {
        Map<String,String> response = new HashMap<>();
        try {
            // 회원가입
            if (userRequestDTO == null) {
                response.put("message", "가입할 회원 정보가 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 신규 회원 정보 디비에 생성
            boolean isJoined = userService.registerUser(userRequestDTO, profileImage);

            if (isJoined) {
                response.put("status", "SUCCESS");
                response.put("message", "회원가입이 완료되었습니다.");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "ERROR");
                response.put("message", "회원가입에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (IOException e) {
            e.printStackTrace(); // 예외 메세지 콘솔 출력
            response.put("message", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 이메일 중복 확인
    @PostMapping("/isExist")
    public ResponseEntity<Boolean> checkEmail(@RequestBody UserRequestDto userRequestDto) {
        return userService.checkEmail(userRequestDto);
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserRequestDto userRequestDTO, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        Map<String, Object> loginResult = userService.login(userRequestDTO);

        if ("SUCCESS".equals(loginResult.get("status"))) {
            User user = (User) loginResult.get("user");

            // 세션에 유저 정보 저장
            session.setAttribute("user_id", user.getId());

            response.put("message", "로그인이 완료되었습니다.");
            response.put("status", "SUCCESS");
            response.put("userId", String.valueOf(user.getId()));

            // 쿠키에 유저 ID 저장
            ResponseCookie responseCookie = ResponseCookie.from("user_id", String.valueOf(user.getId()))
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

//    // 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletRespzonse response) {
//        return userService.logout(request);
//    }

    // 비밀번호 수정
    @PatchMapping("/modifyPassword")
    public ResponseEntity<Boolean> modifyPassword (@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.modifyPassword(userRequestDto));
    }

    // 닉네임 및 프로필 이미지 수정
    // TODO: 엔드포인트 전체 수정 필요 Restful하게  -  userinfo
    @PatchMapping(value = "/modifyNickname", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> modifyNickname (
            @RequestPart(value="request") UserRequestDto userRequestDto,
            @RequestPart(value="file", required = false) MultipartFile profileImage
    ) throws IOException {
        return ResponseEntity.ok(userService.modifyInfo(userRequestDto, profileImage));
    }

    // 닉네임 중복 확인
    @GetMapping("/isExist/{nickname}")
    public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }

    // 유저 정보 조회
    @GetMapping("/loginUser/{id}") // info로 수정
    public ResponseEntity<Map<String, Object>> loginUser(@PathVariable long id) {

        return userService.loginUser(id);
    }

    // 프로필 이미지 불러오기
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfileImage(@PathVariable long userId) {

        return userService.loginUser(userId);
    }

    // 회원탈퇴
    @DeleteMapping("/leaveUser")
    public ResponseEntity<Map<String, Object>> leaveUser(@RequestBody UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        return userService.leaveUser(userRequestDto, request, response);
    }
}
package com.community.chalcak.user.controller;

import com.community.chalcak.jwt.JWTUtil;
import com.community.chalcak.user.dto.JoinDto;
import com.community.chalcak.user.dto.UserRequestDto;
import com.community.chalcak.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final JWTUtil jwtUtil;
    private final UserService userService;

    //회원가입
    @PostMapping(value = "/join", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> join(
            @RequestPart(value = "request") JoinDto joinDto,
            @RequestPart(value = "file", required = false) MultipartFile profileImage
    ) throws IOException {
        Map<String,String> response = new HashMap<>();

        // 이메일 중복 확인
        // true = 중복, false = 사용 가능 이메일
        boolean usedEmail = userService.checkEmail(joinDto);

        // 닉네임 중복 확인
        // true = 중복, false = 사용 가능 닉네임
        boolean usedNickname = userService.checkNickname(joinDto.getNickname());

        if(usedEmail) {
            response.put("message", "중복된 이메일 입니다.");
            return ResponseEntity.badRequest().body(response);
        } else if(usedNickname) {
            response.put("message", "중복된 닉네임 입니다.");
            return ResponseEntity.badRequest().body(response);
        } else {

            try {
                // 회원가입
                if (joinDto == null) {
                    response.put("message", "가입할 회원 정보가 없습니다.");
                    return ResponseEntity.badRequest().body(response);
                }
                // 신규 회원 정보 디비에 생성
                boolean isJoined = userService.registerUser(joinDto, profileImage);

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
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request, HttpServletResponse response) {
        return userService.logout(request);
    }

    // 비밀번호 변경
    @PatchMapping("/password")
    public ResponseEntity<Boolean> modifyPassword (@RequestPart(value="request") UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.modifyPassword(userRequestDto));
    }

    // 닉네임 및 프로필 이미지 변경
    @PatchMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
    @GetMapping("/login/{id}")
    public ResponseEntity<Map<String, Object>> loginUser(@PathVariable long id) {

        return userService.loginUser(id);
    }

    // 프로필 이미지 불러오기
    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfileImage(@PathVariable long userId) {

        return userService.loginUser(userId);
    }

    // 회원탈퇴
    @DeleteMapping("/leave")
    public ResponseEntity<Map<String, Object>> leaveUser(@RequestHeader("Authorization") String token) {
        System.out.println("회원탈퇴 api 호출");
        Map<String, Object> response = new HashMap<>();

        try {
            // Bearer 접두사 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // jwt에서 userId 추출
            Long userId = jwtUtil.getUserId(token);

            // 회원탈퇴
            ResponseEntity<Map<String, Object>> result = userService.leaveUser(userId);

            // 성공 응답
            if (result.getStatusCode().is2xxSuccessful()) {
                response.put("status", "SUCCESS");
                response.put("message", "회원탈퇴가 완료되었습니다.");
                System.out.println("회원탈퇴 완료");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "ERROR");
                response.put("message", result.getBody().get("message"));
                System.out.println("회원탈퇴 오류");
                return new ResponseEntity<>(response, result.getStatusCode());
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", "회원탈퇴 처리 중 오류가 발생했습니다.");
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
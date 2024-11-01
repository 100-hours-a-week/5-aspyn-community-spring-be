package com.community.chalcak.user.service;

import com.community.chalcak.user.dao.UserDao;
import com.community.chalcak.user.entity.User;
import com.community.chalcak.user.dto.UserRequestDto;
import com.community.chalcak.image.service.S3Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDAO;
    private final S3Service s3Service;

    /**
     * 회원가입
     *
     * @param userRequestDTO
     * @return boolean
     * */
    @Transactional
    public boolean registerUser(UserRequestDto userRequestDTO, MultipartFile profileImage) throws IOException {

        String imageUrl = null;

        // 프로필 이미지 있다면 S3에 이미지 업로드
        if (profileImage != null) {
            String imagePath = "profile/";
            imageUrl = s3Service.uploadImage(profileImage,imagePath);
        }

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setNickname(userRequestDTO.getNickname());
        user.setProfileUrl(imageUrl);

        return userDAO.insertUser(user);
    }

    /**
     * 로그인
     *
     * @param userRequestDTO
     * @return Map<String, Object>
     * */
    public Map<String, Object> login(UserRequestDto userRequestDTO) {
        Map<String, Object> result = new HashMap<>();

        // 로그인 시도 이메일과 db 정보 대조하여 데이터 가져옴
        User dbUser = userDAO.checkLogin(userRequestDTO);
        // TODO: 비밀번호 검증 수정 필요
        //  해시 알고리즘 사용 또는 다른 방법
        if (dbUser == null) {
            result.put("status", "ERROR");
            result.put("message", "존재하지 않는 이메일입니다.");
        } else if (dbUser.getDeletedAt() != null) {
            result.put("status", "ERROR");
            result.put("message", "탈퇴한 계정입니다.");
        } else if (!dbUser.getPassword().equals(userRequestDTO.getPassword())){
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
    public boolean modifyPassword(UserRequestDto userRequestDto) {

        User user = new User();
        user.setId(userRequestDto.getId());
        user.setPassword(userRequestDto.getPassword());

        return userDAO.modifyPassword(user);
    }

    // 닉네임 및 프로필 이미지 수정
    @Transactional
    public boolean modifyInfo(UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException {

        User user = new User();
        user.setId(userRequestDto.getId());
        user.setNickname(userRequestDto.getNickname());

        String newProfileUrl = null;
        String preProfileUrl = null;
        if (profileImage != null) {
            // 기존 프로필 url 조회
            Map<String,Object> result = userDAO.getProfileUrl(user.getId());
            preProfileUrl = result.get("profile_url").toString();

            // 새로운 프로필 s3 업로드
            newProfileUrl = s3Service.uploadImage(profileImage, "profile/");
            user.setProfileUrl(newProfileUrl);
        }

        // 프로필 및 닉네임 변경사항 DB 저장
        boolean modifyInfo = userDAO.modifyInfo(user);

        if (profileImage != null) {
            // 기존 프로필 이미지 S3에서 삭제
            s3Service.deleteImage(preProfileUrl);
        }

        return modifyInfo;
    }

    // 닉네임 중복 확인
    public boolean checkNickname(String nickname) {
        return userDAO.existByNickname(nickname);
    }

    // 이메일 중복 확인
    public ResponseEntity<Boolean>checkEmail (UserRequestDto userRequestDto) {

        String email = userRequestDto.getEmail();

        boolean usable = userDAO.checkEmail(email); // true 일시 중복 이메일 존재
        return new ResponseEntity<>(!usable, HttpStatus.OK);
    }

    // 유저 정보 조회
    public ResponseEntity<Map<String, Object>> loginUser(long id) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = userDAO.loginUser(id);

        if (result == null) {
            response.put("message", "유저를 찾을 수 없습니다.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.put("id", result.get("id"));
            response.put("email", result.get("email"));
            response.put("nickname", result.get("nickname"));
            response.put("profileUrl", result.get("profile_url"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    // 회원탈퇴
    public ResponseEntity<Map<String, Object>> leaveUser(UserRequestDto userRequestDto, HttpServletRequest request, HttpServletResponse response) {
        User user = new User();
        user.setId(userRequestDto.getId());

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

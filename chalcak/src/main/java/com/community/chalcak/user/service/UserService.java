package com.community.chalcak.user.service;

import com.community.chalcak.comment.service.CommentService;
import com.community.chalcak.post.service.PostService;
import com.community.chalcak.user.dao.UserDao;
import com.community.chalcak.user.dto.JoinDto;
import com.community.chalcak.user.dto.UserResponseDto;
import com.community.chalcak.user.entity.User;
import com.community.chalcak.user.dto.UserRequestDto;
import com.community.chalcak.image.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDAO;
    private final S3Service s3Service;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PostService postService;
    private final CommentService commentService;

    /**
     * 회원가입
     *
     * @param joinDto
     * @return boolean
     * */
    @Transactional
    public boolean registerUser(JoinDto joinDto, MultipartFile profileImage) throws IOException {

        String imageUrl = null;

        // 프로필 이미지 있다면 S3에 이미지 업로드
        if (profileImage != null) {
            String imagePath = "profile/";
            imageUrl = s3Service.uploadImage(profileImage,imagePath);
        }

        // UserDTO를 User 객체로 변환
        User user = new User();
        user.setEmail(joinDto.getEmail());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setNickname(joinDto.getNickname());
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
        User dbUser = userDAO.findByUsername(userRequestDTO.getEmail());
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

    // 비밀번호 변경
    public boolean modifyPassword(UserRequestDto userRequestDto) {

        User user = new User();
        user.setId(userRequestDto.getId());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        return userDAO.modifyPassword(user);
    }

    // 닉네임 및 프로필 이미지 수정
    @Transactional
    public boolean modifyInfo(UserRequestDto userRequestDto, MultipartFile profileImage) throws IOException {

        User user = new User();
        user.setId(userRequestDto.getId());
        user.setNickname(userRequestDto.getNickname());

        String preProfileUrl = null;
        if (profileImage != null) {
            // 기존 프로필 url 조회
            Map<String,Object> result = userDAO.getProfileUrl(user.getId());
            if (result.get("profile_url") != null) {
                preProfileUrl = result.get("profile_url").toString();
            }

            // 새로운 프로필 s3 업로드
            String newProfileUrl = s3Service.uploadImage(profileImage, "profile/");
            // 엔티티에 새로운 프로필 url 삽입
            user.setProfileUrl(newProfileUrl);
        }

        // 프로필 및 닉네임 변경사항 DB 저장
        boolean modifyInfo = userDAO.modifyInfo(user);

        if (profileImage != null) {
            if (preProfileUrl != null) {
                // 기존 프로필 이미지 S3에서 삭제
                s3Service.deleteImage(preProfileUrl);
            }
        }

        return modifyInfo;
    }

    // 닉네임 중복 확인
    public boolean checkNickname(String nickname) {
        return userDAO.existByNickname(nickname);
    }

    // 이메일 중복 확인
    public Boolean checkEmail (JoinDto joinDto) {

        String email = joinDto.getEmail();

        return userDAO.checkEmail(email); // true 일시 중복 이메일 존재
    }

    // 유저 정보 조회
    public ResponseEntity<Map<String, Object>> loginUser(long id) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> result = userDAO.getUserInfo(id);

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

    public UserResponseDto getUserInfo(long id) {
        Map<String, Object> user = userDAO.getUserInfo(id);

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId((Long) user.get("id"));
        userDto.setEmail(user.get("email").toString());
        userDto.setNickname(user.get("nickname").toString());
        userDto.setProfileUrl((String) user.get("profile_url"));

        return userDto;
    }

    // 회원탈퇴
    @Transactional
    public ResponseEntity<Map<String, Object>> leaveUser(Long userId) {

        Map<String, Object> result = new HashMap<>();

        try {
        // 유저 프로필 이미지 주소 가져오기
        Map<String, Object> user = userDAO.getUserInfo(userId);
        String profileUrl = (String) user.get("profile_url");

        // 게시글 이미지 주소 가져오기 (list)
        List<String> postImgUrls = postService.getPostImgUrlsByUserId(userId);

        // 회원 탈퇴 (유저 활성상태 업데이트 및 프로필 url null 처리)
        Map<String,Object> userLeave = userDAO.leaveUser(userId);

        // 게시글 삭제
        int removePosts = postService.removeLeaveUserPosts(userId);

        // 댓글 삭제
        commentService.removeAllComments(userId);

        // s3에 저장된 프로필 이미지 삭제
        if (profileUrl != null) {
            try{
                s3Service.deleteImage(profileUrl);
            } catch (Exception e) {
                log.error("S3 프로필 이미지 삭제 실패: {}", profileUrl, e);
            }
        }

        // 게시글 이미지 삭제 (list)
        if (removePosts > 0 && postImgUrls != null && !postImgUrls.isEmpty()) {
            for (String postImgUrl : postImgUrls) {
                try{
                    s3Service.deleteImage(postImgUrl);
                } catch (Exception e) {
                    log.error("S3 게시글 이미지 삭제 실패: {}", postImgUrl, e);
                }
            }
        }
        result.put("status", "SUCCESS");
        result.put("message", "회원탈퇴가 완료되었습니다.");
        return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("회원탈퇴 처리 중 오류 발생", e);
            result.put("status", "ERROR");
            result.put("message", "회원탈퇴 처리 중 오류가 발생했습니다.");
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

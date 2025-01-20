package com.community.chalcak.user.service;

import com.community.chalcak.comment.service.CommentService;
import com.community.chalcak.post.service.PostService;
import com.community.chalcak.user.dao.UserDao;
import com.community.chalcak.user.dto.*;
import com.community.chalcak.user.entity.User;
import com.community.chalcak.image.service.S3Service;
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

    // 비밀번호 변경
    public boolean modifyPassword(long userId, RequestChangePwDto requestChangePwDto) {
        String newPassword = passwordEncoder.encode(requestChangePwDto.getPassword());
        return userDAO.modifyPassword(userId, newPassword);
    }

    // 닉네임 및 프로필 이미지 수정
    @Transactional
    public boolean modifyInfo(long userId, RequestChangeInfoDto requestChangeInfoDto, MultipartFile profileImage) throws IOException {

        User user = new User();
        user.setId(userId);
        user.setNickname(requestChangeInfoDto.getNickname());

        String preProfileUrl = null;
        // 변경할 프로필 이미지가 있는 경우
        if (profileImage != null) {
            // 기존 프로필 url 조회
            preProfileUrl = userDAO.getProfileUrl(user.getId());

            // 새로운 프로필 s3 업로드
            String newProfileUrl = s3Service.uploadImage(profileImage, "profile/");

            // 엔티티에 새로운 프로필 url 삽입
            user.setProfileUrl(newProfileUrl);
        }

        // 프로필 및 닉네임 변경사항 DB 저장
        boolean modifyInfo = userDAO.modifyInfo(user);

        // 기존 프로필 이미지 s3에서 삭제
        if (modifyInfo && preProfileUrl != null) {
            s3Service.deleteImage(preProfileUrl);
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

    // 유저 정보 가져오기
    public ResponseUserDto getUserInfo(long id) {
        Map<String, Object> user = userDAO.getUserInfo(id);

        ResponseUserDto userDto = new ResponseUserDto();
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
        String profileUrl = userDAO.getProfileUrl(userId);

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

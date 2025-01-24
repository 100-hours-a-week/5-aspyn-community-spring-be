package com.community.chalcak.post.service;

import com.community.chalcak.image.service.S3Service;
import com.community.chalcak.post.dao.PostDao;
import com.community.chalcak.post.dto.PostResponseDto;
import com.community.chalcak.post.entity.Post;
import com.community.chalcak.post.dto.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final S3Service s3Service;

    @Getter
    @AllArgsConstructor
    public static class PagedPosts {
        private List<PostResponseDto> posts;
        private long totalElements;
        private int totalPages;
    }

    // 게시글 목록 조회
    public Map<String, Object> getAllPosts() {
        Map<String, Object> result = new HashMap<>();
        List<Post> allPosts = postDao.findAllPosts();

//        log.debug("#### 게시글 목록 조회");
        if (allPosts == null) {
            result.put("status", "ERROR");
            result.put("message", "게시글 조회 중 오류가 발생하였습니다.");
        } else {
            result.put("status", "SUCCESS");
            result.put("data", postDao.findAllPosts());
        }
        return result;
    }

    public PagedPosts getPagedPosts (int page, int size) {
        int offset = page * size; // 페이징 시작 위치
        List<Post> pagedPosts = postDao.findPostsByPage(offset, size);

        // 게시글 개수
        long totalElements = postDao.countAllPosts();

        // 전체 페이지 계산
        int totalPages = (int) Math.ceil((double) totalElements / size);

        // 엔티티를 DTO로 변환
        List<PostResponseDto> postResponseDtos = pagedPosts.stream()
                .map(this::convertToPostResponseDto)
                .toList();

        return new PagedPosts(postResponseDtos, totalElements, totalPages);
    }

    // 엔티티를 DTO로 변환하는 메서드
    private PostResponseDto convertToPostResponseDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setText(post.getText());
        dto.setImgUrl(post.getImgUrl());
        dto.setIris(post.getIris());
        dto.setShutterSpeed(post.getShutterSpeed());
        dto.setIso(post.getIso());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setDeletedAt(post.getDeletedAt());
        dto.setUserId(post.getUserId());
        dto.setNickname(post.getNickname());
        dto.setProfileUrl(post.getProfileUrl());
        dto.setComments(post.getComments()); // 댓글 리스트도 설정
        return dto;
    }

    // 특정 게시글 상세 조회 (게시글+댓글)
    public Map<String, Object> getPost(long id) {
        Map<String, Object> result = new HashMap<>();
        Optional<Post> postOptional = postDao.getPost(id);

        if (!postOptional.isPresent()) {
            result.put("status", "ERROR");
            result.put("message", "게시글이 존재하지 않습니다.");
        } else {
            Post dbPost = postOptional.get();
            if (dbPost.getDeletedAt() != null) {
                result.put("status", "ERROR");
                result.put("message", "삭제된 게시글입니다.");
            } else {
                result.put("status", "SUCCESS");
                result.put("post", dbPost);
            }
        }
        return result;
    }

    // 특정 게시글만 불러오기
    public Map<String, Object> getPostOnly(long id) {
        Post post = postDao.getOnlyPost(id);
        Map<String, Object> result = new HashMap<>();

        if (post == null) {
            result.put("status", "ERROR");
            result.put("message", "게시글이 존재하지 않습니다.");
        } else {
            result.put("status", "SUCCESS");
            result.put("post", post);
        }
        return result;
    }

    // 신규 게시글 등록
    @Transactional
    public int newPost(long userId, PostRequestDto postRequestDto, MultipartFile image) throws IOException {

        // 이미지 S3에 업로드
        String imageUrl = s3Service.uploadImage(image, "post/");

        //PostDto 객체를 Post 객체로 변환
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(postRequestDto.getTitle());
        post.setText(postRequestDto.getText());
        post.setImgUrl(imageUrl);
        post.setIris(postRequestDto.getIris());
        post.setShutterSpeed(postRequestDto.getShutterSpeed());
        post.setIso(postRequestDto.getIso());

        return postDao.insertPost(post);
    }

    // 게시글 수정
    public boolean modifyPost(long userId, PostRequestDto postRequestDto) {

        System.out.println("로그인 유저: " + userId);
        System.out.println("게시글 번호: " + postRequestDto.getId());
        Post prePost = postDao.getOnlyPost(postRequestDto.getId());

        System.out.println("이전 게시글 정보: " + prePost);
        long postWriter = prePost.getUserId();

        // 동일한 작성자인지 확인
        if (userId != postWriter) {
            return false;
        }

        System.out.println(postRequestDto.getShutterSpeed());
        System.out.println(postRequestDto.getText());

        //PostDto 객체를 Post 객체로 변환
        Post post = new Post();
        post.setId(postRequestDto.getId());
        post.setTitle(postRequestDto.getTitle());
        post.setText(postRequestDto.getText());
        post.setIris(postRequestDto.getIris());
        post.setShutterSpeed(postRequestDto.getShutterSpeed());
        post.setIso(postRequestDto.getIso());
        post.setUserId(userId); // 작성자

        return postDao.modifyPost(post);
    }

    // 단일 게시글 삭제
    public boolean removePost(long id) {
        // 게시글 이미지 주소
        String imgUrl = postDao.getPostImgurl(id);

        if (imgUrl == null) {
            // 해당 게시글 이미지가 없으면 false 반환
            return false;
        }

        // 게시글 삭제 처리
        boolean remove = postDao.removePost(id);

        if (remove) {
            // s3에 해당 게시글 이미지 삭제
            s3Service.deleteImage(imgUrl);
            return true;
        }
        return false;
    }

    // 특정 유저의 모든 게시글 이미지 주소 가져오기
    public List<String> getPostImgUrlsByUserId(long userId) {
        return postDao.getPostImgUrlsByUserId(userId);
    }

    // 특정 유저의 모든 게시글 일괄 삭제
    public int removeLeaveUserPosts(long userId) {
        return postDao.removeLeaveUserPosts(userId);
    }

}

package com.community.chalcak.post.service;

import com.community.chalcak.image.service.S3Service;
import com.community.chalcak.post.dao.PostDao;
import com.community.chalcak.post.entity.Post;
import com.community.chalcak.post.dto.PostDto;
import com.community.chalcak.post.entity.PostInfo;
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

    // 게시글 목록 조회
    public Map<String, Object> getAllPosts() {
        Map<String, Object> result = new HashMap<>();
        List<PostInfo> allPosts = postDao.findAllPosts();

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

    // 특정 게시글 상세 조회 (게시글+댓글)
    public Map<String, Object> getPost(long id) {
        Map<String, Object> result = new HashMap<>();
        Optional<PostInfo> postOptional = postDao.getPost(id);

        if (!postOptional.isPresent()) {
            result.put("status", "ERROR");
            result.put("message", "게시글이 존재하지 않습니다.");
        } else {
            PostInfo dbPost = postOptional.get();
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
    public Post getOnlyPost(long id) {
        return postDao.getOnlyPost(id);
    }

    // 신규 게시글 등록
    @Transactional
    public int newPost(PostDto postDto, MultipartFile image) throws IOException {

        // 이미지 S3에 업로드
        String imageUrl = s3Service.uploadImage(image, "post/");

        //PostDto 객체를 Post 객체로 변환
        Post post = new Post();
        post.setUserId(postDto.getUserId());
        post.setTitle(postDto.getTitle());
        post.setText(postDto.getText());
        post.setImgUrl(imageUrl);
        post.setIris(postDto.getIris());
        post.setShutterSpeed(postDto.getShutterSpeed());
        post.setIso(postDto.getIso());

        return postDao.insertPost(post);
    }

    // 게시글 수정
    public boolean modifyPost(PostDto postDto) {

        long userId = postDto.getUserId();
        System.out.println(postDto.getId());
        Post prePost = postDao.getOnlyPost(postDto.getId());

        System.out.println(prePost);
        long postWriter = prePost.getUserId();

        // 동일한 작성자인지 확인
        if (userId != postWriter) {
            return false;
        }

        System.out.println(postDto.getShutterSpeed());
        System.out.println(postDto.getText());

        //PostDto 객체를 Post 객체로 변환
        Post post = new Post();
        post.setId(postDto.getId());
        post.setTitle(postDto.getTitle());
        post.setText(postDto.getText());
        post.setIris(postDto.getIris());
        post.setShutterSpeed(postDto.getShutterSpeed());
        post.setIso(postDto.getIso());
        post.setUserId(postDto.getUserId()); // 작성자

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

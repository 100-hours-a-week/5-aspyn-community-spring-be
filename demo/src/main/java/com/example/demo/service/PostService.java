package com.example.demo.service;

import com.example.demo.dao.PostDao;
import com.example.demo.dto.Post;
import com.example.demo.dto.PostDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostDao postDao;
    private final UserService userService;

    public Map<String, Object> getAllPosts() {
        Map<String, Object> result = new HashMap<>();
        List<Post> allPosts = postDao.findAllPosts();

        log.debug("#### 게시글 목록 조회");
        if (allPosts == null) {
            result.put("status", "ERROR");
            result.put("message", "게시글 조회중 오류가 발생하였습니다.");
        } else {
            result.put("status", "SUCCESS");
            result.put("data", postDao.findAllPosts());
        }
        return result;
    }

    public Map<String, Object> getPost(int id) {
        Map<String, Object> result = new HashMap<>();
        List<Post> allPosts = postDao.findAllPosts();

        Optional<Post> postOptional = allPosts.stream()
                .filter(post -> post.getId() == id)
                .findFirst();

        if (!postOptional.isPresent()) {
            result.put("status", "ERROR");
            result.put("message", "게시글이 존재하지 않습니다.");
        } else {
            Post dbPost = postOptional.get();
            if ("Y".equals(dbPost.getDelete())) {
                result.put("status", "ERROR");
                result.put("message", "삭제된 게시글입니다.");
            } else {
                result.put("status", "SUCCESS");
                result.put("post", dbPost);
            }
        }
        return result;
    }

    //회원탈퇴
    @Transactional
    public void deleteMember() {
//        1. 탈퇴회원 게시글 삭제 postDao
//        2. 탈퇴회원 댓글 삭제 postDao
//        3. 회원 삭제 userDao

        // return type int return value 회원id
    }
}

package com.community.chalcak.post.dto;

import com.community.chalcak.comment.entitiy.Comment;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private long id;
    private String title;
    private String text;
    private String imgUrl;
    private String iris; // 조리개
    private String shutterSpeed; // 셔터스피드
    private String iso; // 감도
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private long userId;
    private String nickname; // join 값 가져오기
    private String profileUrl;
    private List<Comment> comments; // 댓글 목록
}

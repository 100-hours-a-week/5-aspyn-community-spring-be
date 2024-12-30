package com.community.chalcak.post.entity;

import com.community.chalcak.comment.entitiy.Comment;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class PostInfo {
    private long id; // 게시글 id
    private String title;
    private String text; // 게시글 내용
    private String imgUrl;
    private String iris; // 조리개
    private String shutterSpeed; // 셔터스피드
    private String iso; // 감도
    private Timestamp updatedAt;
    private Timestamp deletedAt;
    private long userId; // 작성 유저
    private String nickname;
    private String profileUrl;
    private List<Comment> comments; // 댓글 목록
}

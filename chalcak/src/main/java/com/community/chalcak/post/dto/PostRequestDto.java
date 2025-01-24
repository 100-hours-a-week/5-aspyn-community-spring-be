package com.community.chalcak.post.dto;

import com.community.chalcak.comment.entitiy.Comment;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter @Setter
public class PostRequestDto {
    private long id;
    private String title;
    private String text;
    private String iris; // 조리개
    private String shutterSpeed; // 셔터스피드
    private String iso; // 감도
}

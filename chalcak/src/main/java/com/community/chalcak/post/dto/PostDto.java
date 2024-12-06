package com.community.chalcak.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter @Setter
public class PostDto {
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
    private Date delete_date;
    private String nickname; // join 값 가져오기
}

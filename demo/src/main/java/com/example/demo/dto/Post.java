package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Post {
    private int Id; // 게시글 id
    private String title;
    private String text; // 게시글 내용
    private int like;
    private int comment;
    private int view;
    private Date write_date;
    private Date modify_date;
    private String delete;
    private int user_num; // 작성 유저
    private Date delete_date;
    private String nickname;
    private List<Comment> comments; // 댓글 목록
}

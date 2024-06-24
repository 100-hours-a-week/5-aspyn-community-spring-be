package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Post {
    private int Id;
    private String title;
    private String text; // 게시글 내용
    private int like;
    private int comment;
    private int view;
    private Date write_date;
    private Date modify_date;
    private String delete;
    private int user_num;
    private Date delete_date;
    private String nickname;
}

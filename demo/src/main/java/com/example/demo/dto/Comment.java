package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comment {
    private int seq;
    private String text;
    private Date writeDate;
    private Date modifyDate;
    private String delete;
    private int userNum;
    private int postId;
    private Date deleteDate;
    private String nickname;
    private String leave;
}

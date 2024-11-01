package com.community.chalcak.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class CommentDto {
    private long id;
    private String text;
    private Date writeDate;
    private Date modifyDate;
    private String delete;
    private long userId;
    private long postId;
}

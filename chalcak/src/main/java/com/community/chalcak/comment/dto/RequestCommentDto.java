package com.community.chalcak.comment.dto;

import lombok.Getter;

@Getter
public class RequestCommentDto {
    private long id; // 댓글 아이디
    private String text;
    private long postId;
}

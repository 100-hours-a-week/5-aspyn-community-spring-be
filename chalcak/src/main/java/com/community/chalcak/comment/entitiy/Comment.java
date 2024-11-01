package com.community.chalcak.comment.entitiy;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class Comment {
    private Long id;
    private String text;
    private Long userId;
    private Long postId;
    private Timestamp updatedAt;
    private String nickname;
    private String profileUrl;
    private Timestamp userDeletedAt; //  유저 탈퇴 여부
}

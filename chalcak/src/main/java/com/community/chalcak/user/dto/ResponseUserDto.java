package com.community.chalcak.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ResponseUserDto {
    private long id;
    private String email;
    private String nickname;
    private String profileUrl;
    private LocalDateTime deletedAt;
}

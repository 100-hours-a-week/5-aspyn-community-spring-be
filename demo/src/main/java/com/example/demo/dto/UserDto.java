package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {
    private long id;
    private String email;
    private String password;
    private String nickname;
    private String profileUrl;
}

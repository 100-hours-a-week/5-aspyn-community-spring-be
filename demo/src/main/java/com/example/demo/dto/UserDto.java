package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDto {
    private int user_num;
    private String email;
    private String password;
    private String nickname;
}

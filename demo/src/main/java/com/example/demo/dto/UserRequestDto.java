package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequestDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String ProfileUrl;
}

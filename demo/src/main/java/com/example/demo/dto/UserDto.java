package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;
}

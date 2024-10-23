package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private long id;
    private String email;
    private String password;
    private String nickname;
    private String profileUrl;
    private Date createdAt;
    private Date deletedAt;
}

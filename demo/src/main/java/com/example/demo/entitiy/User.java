package com.example.demo.entitiy;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private long id;
    private String email;
    private String password;
    private String nickname;
    private String profileUrl;
    private Timestamp createdAt;
    private Timestamp deletedAt;
}

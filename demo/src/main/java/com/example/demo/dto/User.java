package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private int user_num;
    private String email;
    private String password;
    private String nickname;
    private String leave;
    private Date join_date;
    private String leave_date;
}

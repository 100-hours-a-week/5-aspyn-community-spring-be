package com.example.demo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User {
    private int user_num;
    private String Email;
    private String Password;
    private String Nickname;
    private String Leave;
    private Date join_date;
    private String leave_date;
}

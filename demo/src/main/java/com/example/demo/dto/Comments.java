package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Comments {
    private int seq;
    private String text;
    private Date write_date;
    private Date modify_date;
    private String delete;
    private int user_num;
    private int post_id;
    private Date delete_date;
}

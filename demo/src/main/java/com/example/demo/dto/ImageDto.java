package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ImageDto {
    private long id;
    private int userId;
    private int postId;
    private String name;
    private String path;
}

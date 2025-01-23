package com.community.chalcak.post.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostGetDto {

    @Min(1)
    private int page = 1; // 요청 들어온 현재 페이지
    @Min(1)
    private int size = 10;
}

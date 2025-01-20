package com.community.chalcak.user.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class RequestChangeInfoDto {

    @Pattern(regexp = "/^[ㄱ-ㅎ가-힣a-zA-Z0-9]+$/")
    private String nickname;
}

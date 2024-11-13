package com.community.chalcak.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRequestDto {

    @NotBlank
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "/^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$/")
    private String password;

    @NotBlank
    @Pattern(regexp = "/^[ㄱ-ㅎ가-힣a-zA-Z0-9]+$/")
    private String nickname;

    private String ProfileUrl;
}

package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class UserController {
    private final JoinService joinService;

    @Autowired
    public UserController(JoinService joinService) {
        this.joinService = joinService;
    }

    // @PostMapping 이랑 같은 기능
    // @RequestMapping(method = RequestMethod.POST, path = "/user/join")
    @PostMapping("/user/join")
    public String join(@RequestBody UserDTO userDTO) throws SQLException {
        try {
            // 회원가입 로직 작성 필요
            if (userDTO == null) {
                return "가입할 회원 정보가 없습니다.";
            }
            // 신규 회원 정보 디비에 생성
            boolean isJoined = joinService.registerUser(userDTO);

            if (isJoined) {
                return "회원가입이 완료되었습니다.";
            } else {
                return "회원가입에 실패했습니다.";
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 예외 메세지 콘솔 출력
            return "회원가입 중 오류가 발생했습니다.";
        }
    }
}
package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;

@RequestMapping("/api") // 이곳으로 들어오는 API 주소를 mapping
public class UserController {
    @Autowired
    private final JoinService joinService;

    public UserController(JoinService joinService) {
        this.joinService = joinService;
    }

    // @PostMapping 이랑 같은 기능
    // @RequestMapping(method = RequestMethod.POST, path = "/user/join")
    @PostMapping("/user/join")
    public String join(@RequestBody UserDTO userDTO) throws SQLException {
        boolean isJoined = joinService.registerUser(userDTO);

        if (isJoined) {
            return "회원가입이 완료되었습니다.";
        } else {
            return "회원가입에 실패했습니다.";
        }


        //private UserService userService;
        //private JoinService joinService; // 이렇게 만들면 안되나? 포스트매핑은 하나씩만 해야 하나?
        // 모델 호출 -> 서비스
        // 반환 값 확인
        // 회원가입이 완료되었습니다. or 회원가입에 실패했습니다.
    }
}

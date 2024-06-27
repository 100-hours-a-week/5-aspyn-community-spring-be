package com.example.demo.controller;

import com.example.demo.service.CmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cmt")
public class CmtController {
    private final CmtService cmtService;

    // 댓글 작성

    // 댓글 수정

    // 댓글 삭제
    @DeleteMapping("/remove/{seq}")
    public ResponseEntity<Map<String, Object>> removeCmt(@PathVariable int seq) {
        Map<String, Object> response = new HashMap<>();
        boolean isRemoved = cmtService.removeCmt(seq);

        if (isRemoved) {
            response.put("status", "SUCCESS");
            response.put("message", "댓글이 삭제되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "ERROR");
            response.put("message", "댓글 삭제에 실패했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

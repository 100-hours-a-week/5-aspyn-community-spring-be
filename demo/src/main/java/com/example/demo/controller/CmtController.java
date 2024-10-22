package com.example.demo.controller;

import com.example.demo.dto.CmtDto;
import com.example.demo.dto.Comment;
import com.example.demo.service.CmtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/comment")
public class CmtController {
    private final CmtService cmtService;

    // 댓글 등록
    @PostMapping("/edit")
    public ResponseEntity<Map<String, Object>> newCmt(@RequestBody CmtDto cmtDto) {
        Map<String, Object> response = new HashMap<>();

        boolean newCmt = cmtService.insertCmt(cmtDto);

        if (newCmt) {
            response.put("status", "SUCCESS");
            response.put("message", "댓글이 등록 되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "ERROR");
            response.put("message", "댓글 등록에 실패했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 불러오기
    @GetMapping("/{seq}")
    public ResponseEntity<Map<String, Object>> getCmt(@PathVariable("seq") int seq) {
        return cmtService.getCmt(seq);
    }

    // 댓글 수정
    @PatchMapping("/modify")
    public ResponseEntity<Map<String, Object>> modifyCmt(@RequestBody CmtDto cmtDto) {
        Map<String, Object> response = new HashMap<>();

        boolean modifyCmt = cmtService.modifyCmt(cmtDto);

        if (modifyCmt) {
            response.put("status", "SUCCESS");
            response.put("message", "댓글 수정이 완료되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("status", "ERROR");
            response.put("message", "댓글 수정에 실패했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

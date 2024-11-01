package com.community.chalcak.comment.controller;

import com.community.chalcak.comment.dto.CommentDto;
import com.community.chalcak.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("/edit")
    public ResponseEntity<Map<String, Object>> newCmt(@RequestBody CommentDto commentDto) {
        Map<String, Object> response = new HashMap<>();

        boolean newCmt = commentService.insertCmt(commentDto);

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
        return commentService.getCmt(seq);
    }

    // 댓글 수정
    @PatchMapping("/modify")
    public ResponseEntity<Map<String, Object>> modifyCmt(@RequestBody CommentDto commentDto) {
        Map<String, Object> response = new HashMap<>();

        boolean modifyCmt = commentService.modifyCmt(commentDto);

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
        boolean isRemoved = commentService.removeCmt(seq);

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

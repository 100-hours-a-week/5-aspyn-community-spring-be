package com.community.chalcak.comment.controller;

import com.community.chalcak.comment.dto.RequestCommentDto;
import com.community.chalcak.comment.service.CommentService;
import com.community.chalcak.user.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/comment")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping(value = "/edit")
    public ResponseEntity<Map<String, Object>> newComment(@RequestBody RequestCommentDto commentDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        long userId = userDetails.getUserId();
        Map<String, Object> response = new HashMap<>();

        boolean newComment = commentService.insertComment(userId, commentDto);

        if (newComment) {
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
    public ResponseEntity<Map<String, Object>> getComment(@PathVariable("seq") int seq, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return commentService.getComment(seq);
    }

    // 댓글 수정
    @PatchMapping("/modify")
    public ResponseEntity<Map<String, Object>> modifyComment(@RequestBody RequestCommentDto commentDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Map<String, Object> response = new HashMap<>();

        boolean modifyComment = commentService.modifyComment(commentDto);

        if (modifyComment) {
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
    @DeleteMapping("/{seq}")
    public ResponseEntity<Map<String, Object>> removeComment(@PathVariable int seq) {
        Map<String, Object> response = new HashMap<>();
        boolean isRemoved = commentService.removeComment(seq);

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

package com.community.chalcak.comment.service;

import com.community.chalcak.comment.dao.CommentDao;
import com.community.chalcak.comment.dto.CommentDto;
import com.community.chalcak.comment.entitiy.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentDao commentDao;

    // 댓글 등록
    public boolean insertCmt(CommentDto commentDto) {

        Comment comment = new Comment();
        comment.setPostId(commentDto.getPostId());
        comment.setText(commentDto.getText());
        comment.setUserId(commentDto.getUserId());

        return commentDao.insertCmt(comment);
    }

    // 댓글 불러오기
    public ResponseEntity<Map<String, Object>>getCmt(long id){
       try {
           Map<String,Object> cmtList = commentDao.getComment(id);
           // 댓글이 있으면 성공 응답 반환
           if (cmtList != null && !cmtList.isEmpty()) {
               Map<String, Object> response = new HashMap<>();
               response.put("text", cmtList.get("text"));
               return new ResponseEntity<>(response, HttpStatus.OK);
           } else {
               // 댓글이 없으면 404 반환
               Map<String, Object> errorResponse = new HashMap<>();
               errorResponse.put("message", "댓글을 찾을 수 없습니다.");
               return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
           }
       } catch (Exception e) {
           // 오류가 발생하면 500 응답을 반환
           Map<String, Object> errorResponse = new HashMap<>();
           errorResponse.put("message", "서버 오류가 발생했습니다.");
           return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
       }

    }

    // 댓글 수정
    public boolean modifyCmt(CommentDto commentDto) {

        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());

        return commentDao.modifyCmt(comment);
    }

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        return commentDao.removeCmt(seq);
    }

    // 탈퇴 유저의 모든 댓글 일괄 삭제
    public void removeAllComments(long userId) {
        commentDao.removeAllComments(userId);
    }
}

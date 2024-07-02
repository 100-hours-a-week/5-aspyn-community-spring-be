package com.example.demo.service;

import com.example.demo.dao.CmtDao;
import com.example.demo.dto.CmtDto;
import com.example.demo.dto.Comment;
import com.example.demo.dto.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CmtService {
    private final CmtDao cmtDao;

    // 댓글 등록
    public boolean insertCmt(CmtDto cmtDto) {

        Comment comment = new Comment();
        comment.setPostId(cmtDto.getPostId());
        comment.setText(cmtDto.getText());
        comment.setUserNum(cmtDto.getUserNum());

        return cmtDao.insertCmt(comment);
    }

    // 댓글 불러오기
    public ResponseEntity<Map<String, Object>>getCmt(int seq){
       try {
           Map<String,Object> cmtList = cmtDao.getCmt(seq);
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
    public boolean modifyCmt(CmtDto cmtDto) {

        Comment comment = new Comment();
        comment.setSeq(cmtDto.getSeq());
        comment.setText(cmtDto.getText());

        return cmtDao.modifyCmt(comment);
    }

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        return cmtDao.removeCmt(seq);
    }
}

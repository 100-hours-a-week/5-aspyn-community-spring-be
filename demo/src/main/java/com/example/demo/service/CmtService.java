package com.example.demo.service;

import com.example.demo.dao.CmtDao;
import com.example.demo.dto.CmtDto;
import com.example.demo.dto.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        return cmtDao.removeCmt(seq);
    }
}

package com.example.demo.service;

import com.example.demo.dao.CmtDao;
import com.example.demo.dto.Comments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CmtService {
    private final CmtDao cmtDao;

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        return cmtDao.removeCmt(seq);
    }
}

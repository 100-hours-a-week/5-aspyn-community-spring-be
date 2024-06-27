package com.example.demo.dao;

import com.example.demo.dto.Comments;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CmtDao {
    private final JdbcTemplate jdbcTemplate;

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        String sql = "UPDATE comments SET `delete` = 'Y', delete_date = now() " +
                " WHERE seq = ?";

        int result = jdbcTemplate.update(sql,seq);

        return result > 0;
    }

}

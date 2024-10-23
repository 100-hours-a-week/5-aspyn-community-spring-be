package com.example.demo.dao;

import com.example.demo.entitiy.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CmtDao {
    private final JdbcTemplate jdbcTemplate;

    // 댓글 등록
    public boolean insertCmt(Comment comment) {
        String sql = "INSERT INTO comments (post_id, text, user_num) " +
                "VALUES (?, ?, ?)";

        int result = jdbcTemplate.update(sql,comment.getPostId(), comment.getText(), comment.getUserNum());
        return result > 0;
    }

    // 댓글 정보 불러오기
    public Map<String, Object>getCmt(int seq) {
        String sql = "SELECT text FROM comments WHERE seq = ?";
        return jdbcTemplate.queryForMap(sql,seq);
    }

    // 댓글 수정
    public boolean modifyCmt(Comment comment) {
        String sql = "UPDATE comments SET text = ?, modify_date = now() " +
                " WHERE seq = ?";
        int result = jdbcTemplate.update(sql,comment.getText(), comment.getSeq());
        return result > 0 ;
    }

    // 댓글 삭제
    public boolean removeCmt(int seq) {
        String sql = "UPDATE comments SET `delete` = 'Y', delete_date = now() " +
                " WHERE seq = ?";

        int result = jdbcTemplate.update(sql,seq);

        return result > 0;
    }

}

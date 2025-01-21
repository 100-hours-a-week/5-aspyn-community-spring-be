package com.community.chalcak.comment.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommentDao {
    private final JdbcTemplate jdbcTemplate;

    // 댓글 등록
    public boolean insertComment(long postId, String text, long userId) {
        String sql = "INSERT INTO comments (post_id, text, user_id) " +
                "VALUES (?, ?, ?)";

        int result = jdbcTemplate.update(sql,postId, text, userId);
        return result > 0;
    }

    // 댓글 정보 불러오기
    public Map<String, Object>getComment(long id) {
        String sql = "SELECT text FROM comments WHERE id = ?";
        return jdbcTemplate.queryForMap(sql,id);
    }

    // 댓글 수정
    public boolean modifyComment(long id, String text) {
        String sql = "UPDATE comments SET text = ?, updated_at = now() " +
                " WHERE id = ?";
        int result = jdbcTemplate.update(sql,text, id);
        return result > 0 ;
    }

    // 댓글 삭제 (미노출)
    public boolean removeComment(long id) {
        String sql = "UPDATE comments SET deleted_at = now() " +
                " WHERE id = ?";

        int result = jdbcTemplate.update(sql,id);

        return result > 0;
    }

    // 탈퇴회원 댓글 삭제 (미노출)
    public void removeAllComments(long userId) {
        String sql = "UPDATE comments SET deleted_at = now() WHERE user_id = ? and deleted_at IS NULL";
        jdbcTemplate.update(sql,userId);
    }

}

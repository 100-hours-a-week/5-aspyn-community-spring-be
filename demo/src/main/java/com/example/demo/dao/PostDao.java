package com.example.demo.dao;

import com.example.demo.dto.Comments;
import com.example.demo.dto.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostDao {

    private final JdbcTemplate jdbcTemplate;

    // post 객체에 값 삽입
    private static class PostRowMapper implements RowMapper<Post> {
        @Override
        public Post mapRow(ResultSet rs, int rowNum) throws SQLException {
            Post post = new Post();
            post.setId(rs.getInt("Id"));
            post.setTitle(rs.getString("title"));
            post.setText(rs.getString("text"));
            post.setLike(rs.getInt("like"));
            post.setComment(rs.getInt("comment"));
            post.setView(rs.getInt("view"));
            post.setWrite_date(rs.getTimestamp("write_date"));
            post.setModify_date(rs.getTimestamp("modify_date"));
            post.setDelete(rs.getString("delete"));
            post.setUser_num(rs.getInt("user_num"));
            post.setDelete_date(rs.getTimestamp("delete_date"));
            post.setNickname(rs.getString("nickname"));
            return post;
        }
    }

    // 전체 게시글 조회
    public List<Post> findAllPosts() {
        String sql = "SELECT p.Id, p.title, p.text, p.`like`, p.comment, p.view, p.write_date, p.modify_date, " +
                "p.`delete`, p.user_num, p.delete_date, u.nickname " +
                "FROM post p JOIN user u ON u.user_num = p.user_num " +
                "WHERE p.`delete`= 'N'";

        try {
            return jdbcTemplate.query(sql, new PostRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 특정 게시글만 조회
    public List<Post>getOnlyPost(int id) {
        String sql = "SELECT Id, title, text, `delete`, user_num FROM post  WHERE Id = ?";

        try {
            return jdbcTemplate.query(sql, new PostRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 특정 게시글 및 댓글 조회
    public Optional<Post> getPost(int id) {
        String sql =
                "SELECT p.*, u.nickname, u.`leave` FROM post p" +
                " JOIN user u ON p.user_num = u.user_num " +
                " WHERE p.Id = ? AND u.`leave` = 'N'";

        String sql2 = "SELECT * FROM comments c JOIN user u ON c.user_num = u.user_num " +
                " WHERE c.post_id = ? AND c.`delete`='N' AND u.`leave`='N'";
        try {
            List<Post> posts = jdbcTemplate.query(sql, new Object[]{id}, new PostRowMapper());

            if (!posts.isEmpty()) {
                Post postResult = posts.get(0);
                List<Comments> comments = jdbcTemplate.query(sql2, new Object[]{postResult.getId()}, (rs, rowNum) -> {
                    Comments dbComments = new Comments();
                    dbComments.setSeq(rs.getInt("seq"));
                    dbComments.setPostId(rs.getInt("post_id"));
                    dbComments.setText(rs.getString("text"));
                    dbComments.setModifyDate(rs.getTimestamp("modify_date"));
                    dbComments.setDelete(rs.getString("delete"));
                    dbComments.setUserNum(rs.getInt("user_num"));
                    dbComments.setNickname(rs.getString("nickname"));
                    dbComments.setLeave(rs.getString("leave"));
                    return dbComments;
                });
                postResult.setComments(comments); // Assuming Post has a setComments method
                return Optional.of(postResult);
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 신규 게시글 작성
    public boolean insertPost(Post post) {
        String sql = "INSERT INTO post (title, text, user_num) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getUser_num());

        return result > 0;
    }

    // 게시글 수정
    public boolean modifyPost(Post post) {
        String sql = "UPDATE post SET title = ?, text = ?, modify_date = now() " +
                " WHERE Id = ?";
        int result = jdbcTemplate.update(sql, post.getTitle(), post.getText(), post.getId());

        return result > 0;
    }

    // 게시글 삭제
    public boolean removePost(int id) {
        String sql = "UPDATE post SET `delete` = 'Y', delete_date = now() " +
                " WHERE Id = ?";

        int result = jdbcTemplate.update(sql,id);

        return result > 0;
    }
}

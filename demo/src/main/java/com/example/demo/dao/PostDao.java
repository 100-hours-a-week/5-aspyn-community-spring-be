package com.example.demo.dao;

import com.example.demo.dto.Post;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PostDao {

    private final JdbcTemplate jdbcTemplate;

    public PostDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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

    public List<Post> getPost(Post post) {
        String sql = "SELECT * FROM post WHERE Id = ? AND `delete` = 'N'";
        try {
            return jdbcTemplate.query(sql, new Object[]{post.getId()}, new PostRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

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
}

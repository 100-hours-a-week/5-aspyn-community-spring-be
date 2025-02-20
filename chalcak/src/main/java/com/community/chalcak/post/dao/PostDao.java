package com.community.chalcak.post.dao;

import com.community.chalcak.comment.entitiy.Comment;
import com.community.chalcak.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
            post.setId(rs.getInt("id"));
            post.setTitle(rs.getString("title"));
            post.setText(rs.getString("text"));
            post.setImgUrl(rs.getString("img_url"));
            post.setIris(rs.getString("iris"));
            post.setShutterSpeed(rs.getString("shutter_speed"));
            post.setIso(rs.getString("iso"));
            post.setUpdatedAt(rs.getTimestamp("updated_at"));
            post.setUserId(rs.getInt("user_id"));
            post.setNickname(rs.getString("nickname"));
            post.setProfileUrl(rs.getString("profile_url"));
            return post;
        }
    }

    // 전체 게시글 조회
    public List<Post> findAllPosts() {
        String sql = "SELECT p.id, p.title, p.text, p.img_url, p.iris, p.shutter_speed, p.iso, " +
                "p.updated_at, p.user_id, u.nickname, u.profile_url " +
                "FROM post p JOIN user u ON u.id = p.user_id " +
                "WHERE p.deleted_at IS NULL";

        try {
            return jdbcTemplate.query(sql, new PostRowMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 페이지네이션
    public List<Post> findPostsByPage(int offset, int limit) {
        String sql = "SELECT p.id, p.title, p.text, p.img_url, p.iris, p.shutter_speed, p.iso, " +
                "p.updated_at, p.user_id, u.nickname, u.profile_url " +
                "FROM post p JOIN user u ON u.id = p.user_id " +
                "WHERE p.deleted_at IS NULL ORDER BY p.id DESC LiMIT ? OFFSET ?";

        try {
            return jdbcTemplate.query(sql, new PostRowMapper(), limit, offset);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 게시글 개수(삭제되지 않은 건)
    public long countAllPosts() {
        String sql = "SELECT COUNT(*) FROM post WHERE deleted_at IS NULL";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // 특정 게시글만 조회
    public Post getOnlyPost(long id) {
        String sql = "SELECT p.id, p.title, p.text, p.img_url, p.iris, p.shutter_speed, p.iso, p.updated_at, p.user_id, u.nickname, u.profile_url " +
                "FROM post p JOIN user u ON p.user_id = u.id " +
                "WHERE p.id = ? and p.deleted_at IS NULL";

        try {
            return jdbcTemplate.queryForObject(sql, new PostRowMapper(), id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 특정 게시글 및 댓글 조회
    public Optional<Post> getPost(long id) {
        String sql =
                "SELECT p.*, u.nickname, u.profile_url, u.deleted_at FROM post p" +
                " JOIN user u ON p.user_id = u.id " +
                " WHERE p.id = ? AND u.deleted_at IS NULL";

        String sql2 = "SELECT * FROM comments c JOIN user u ON c.user_id = u.id " +
                " WHERE c.post_id = ? AND c.deleted_at IS NULL AND u.deleted_at IS NULL";
        try {
            return jdbcTemplate.query(sql, new Object[]{id}, rs -> {
                if (rs.next()) {
                    Post postInfo = new Post();
                    postInfo.setId(rs.getLong("id"));
                    postInfo.setTitle(rs.getString("title"));
                    postInfo.setText(rs.getString("text"));
                    postInfo.setImgUrl(rs.getString("img_url"));
                    postInfo.setIris(rs.getString("iris"));
                    postInfo.setShutterSpeed(rs.getString("shutter_speed"));
                    postInfo.setIso(rs.getString("iso"));
                    postInfo.setUpdatedAt(rs.getTimestamp("updated_at"));
                    postInfo.setDeletedAt(rs.getTimestamp("deleted_at"));
                    postInfo.setUserId(rs.getLong("user_id"));
                    postInfo.setNickname(rs.getString("nickname"));
                    postInfo.setProfileUrl(rs.getString("profile_url"));

                    // 댓글 조회
                    List<Comment> comments = jdbcTemplate.query(sql2, new Object[]{postInfo.getId()}, (commentRs, rowNum) -> {
                        Comment comment = new Comment();
                        comment.setId(commentRs.getLong("id"));
                        comment.setText(commentRs.getString("text"));
                        comment.setPostId(commentRs.getLong("post_id"));
                        comment.setUserId(commentRs.getLong("user_id"));
                        comment.setUpdatedAt(commentRs.getTimestamp("updated_at"));
                        comment.setNickname(commentRs.getString("nickname"));
                        comment.setProfileUrl(commentRs.getString("profile_url"));
                        return comment;
                    });

                    postInfo.setComments(comments);
                    return Optional.of(postInfo);
                }
                return Optional.empty();
            });
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // 신규 게시글 작성
    public int insertPost(Post post) {
        String sql = "INSERT INTO post (title, text, img_url, user_id, iris, shutter_speed, iso) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, post.getImgUrl());
            ps.setLong(4, post.getUserId());
            ps.setString(5, post.getIris());
            ps.setString(6, post.getShutterSpeed());
            ps.setString(7, post.getIso());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }


    // 게시글 수정 - 이미지 수정 불가
    public boolean modifyPost(Post post) {
        String sql = "UPDATE post SET title = ?, text = ?, iris = ?, shutter_speed = ?, iso = ? " +
                " WHERE id = ?";
        int result = jdbcTemplate.update(sql, post.getTitle(), post.getText(),
                post.getIris(), post.getShutterSpeed(), post.getIso(), post.getId());

        return result > 0;
    }

    // 단일 게시글 이미지 주소 가져오기
    public String getPostImgurl (long id) {
        String sql = "SELECT img_url FROM post WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, String.class, id);
        } catch (EmptyResultDataAccessException e) {
            return null; // 데이터가 없으면 null 반환
        }
    }

    // 단일 게시글 삭제(미노출)
    public boolean removePost(long id) {
        String sql = "UPDATE post SET deleted_at = now(), img_url = '-' WHERE id = ?";
        int result = jdbcTemplate.update(sql,id);
        return result > 0;
    }

    // 탈퇴 회원 게시글 이미지 url 조회
    public List<String> getPostImgUrlsByUserId(long userId) {
        String sql = "SELECT img_url FROM post WHERE user_id = ? and deleted_at IS NULL";
        return jdbcTemplate.queryForList(sql, String.class, userId);
    }

    // 탈퇴 회원 게시글 삭제(미노출)
    public int removeLeaveUserPosts(long userId) {
        String sql = "UPDATE post SET deleted_at = now(), img_url = '-' WHERE user_id = ? and deleted_at IS NULL";
        int result = jdbcTemplate.update(sql,userId);
        return result;
    }

}

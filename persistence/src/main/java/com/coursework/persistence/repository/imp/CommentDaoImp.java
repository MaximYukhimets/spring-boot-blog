package com.coursework.persistence.repository.imp;

import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.CommentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentDaoImp implements CommentDao {

    private final JdbcTemplate jdbcTemplate;

    RowMapper<Comment> rowMapper = (rs, rowNum) -> {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setBody(rs.getString("body"));
        comment.setUserId(rs.getLong("user_id"));
        Timestamp date = (Timestamp) rs.getObject("creation_date");
        comment.setCreationDate(date.toLocalDateTime());
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    };

    @Autowired
    public CommentDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> findAll() {
        return jdbcTemplate.query("SELECT * FROM comments", rowMapper);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = """
                SELECT id, body, user_id, creation_date, post_id
                FROM comments
                WHERE id = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<Comment> findByCreationDate(Timestamp date) {
        String sql = """
                SELECT id, body, user_id, creation_date, post_id
                FROM comments
                WHERE creation_date = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, date).stream().findFirst();
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        String sql = """
                SELECT id, body, user_id, creation_date, post_id
                FROM comments
                WHERE user_id = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        String sql = """
                SELECT id, body, user_id, creation_date, post_id
                FROM comments
                WHERE post_id = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, postId);
    }

    @Override
    public int save(Comment comment) {
        String sql = """
                INSERT INTO comments(body, user_id, creation_date, post_id)
                VALUES (?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                comment.getBody(),
                comment.getUserId(),
                Timestamp.valueOf(comment.getCreationDate()),
                comment.getPostId());
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM comments WHERE id = ?", id);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return jdbcTemplate.update("DELETE FROM comments WHERE user_id = ?", userId);
    }

    @Override
    public int deleteByPostId(Long postId) {
        return jdbcTemplate.update("DELETE FROM comments WHERE post_id = ?", postId);
    }

    @Override
    public Integer getNumberOfEntityByPostId(Long id) {
        String sql = """
                SELECT count(*)
                FROM comments
                WHERE post_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.TYPE, id);
    }

    @Override
    public Integer getNumberOfEntityByUserId(Long id) {
        String sql = """
                SELECT count(*)
                FROM comments
                WHERE user_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.TYPE, id);
    }

    @Override
    public Integer getNumberOfEntity() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM comments", Integer.TYPE);
    }
}

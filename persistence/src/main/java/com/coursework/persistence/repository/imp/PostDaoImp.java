package com.coursework.persistence.repository.imp;

import com.coursework.domain.entity.Post;
import com.coursework.persistence.repository.PostDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class PostDaoImp implements PostDao {

    private final JdbcTemplate jdbcTemplate;

    RowMapper<Post> rowMapper = (rs, rowNum) -> {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setBody(rs.getString("body"));
        Timestamp date = (Timestamp) rs.getObject("creation_date");
        post.setCreationDate(date.toLocalDateTime());
        post.setUserId(rs.getLong("user_id"));
        post.setImage(rs.getBytes("image"));
        return post;
    };

    @Autowired
    public PostDaoImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> findAll() {
        return jdbcTemplate.query("SELECT * FROM posts", rowMapper);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String sql = """
                SELECT id, title, body, creation_date, user_id, image
                FROM posts
                WHERE id = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public List<Post> findByUserId(Long userId) {
        String sql = """
                SELECT id, title, body, creation_date, user_id, image
                FROM posts
                WHERE user_id = ?;
                """;
        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public int save(Post post) {
        String sql = """
                INSERT INTO posts(title, body, creation_date, user_id, image)
                VALUES (?, ?, ?, ?, ?)
                """;
        return jdbcTemplate.update(sql,
                post.getTitle(),
                post.getBody(),
                Timestamp.valueOf(post.getCreationDate()),
                post.getUserId(),
                post.getImage());
    }

    @Override
    public int update(Post post) {
        String sql = """
                UPDATE posts SET title = ?, body = ?, creation_date = ?, user_id = ?, image = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql,
                post.getTitle(),
                post.getBody(),
                Timestamp.valueOf(post.getCreationDate()),
                post.getUserId(),
                post.getImage(),
                post.getId());
    }

    @Override
    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return jdbcTemplate.update("DELETE FROM posts WHERE user_id = ?", userId);
    }

    @Override
    public Integer getNumberOfEntity() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM posts", Integer.TYPE);
    }

    @Override
    public Integer getNumberOfEntityByUserId(Long id) {
        String sql = """
                SELECT count(*)
                FROM posts
                WHERE user_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.TYPE, id);
    }

    @Override
    public List<Post> findAllWithLimitAndOffsetOrderByDataDESC(int limit, int offset) {
        String sql = """
                SELECT *
                FROM posts
                ORDER BY creation_date DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    @Override
    public List<Post> findByUserIdWithLimitAndOffsetOrderByDataDESC(Long userId, int limit, int offset) {
        String sql = """
                SELECT *
                FROM posts
                WHERE user_id = ?
                ORDER BY creation_date DESC
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, rowMapper, userId, limit, offset);
    }

    @Override
    public List<Post> findAllWithLimitAndOffsetOrderByLikeDESC(int limit, int offset) {
        String sql = """
                SELECT *
                FROM posts
                ORDER BY (SELECT count(post_id) FROM users_posts WHERE post_id = posts.id) DESC, posts.id
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, rowMapper, limit, offset);
    }

    @Override
    public List<Post> findByUserIdWithLimitAndOffsetOrderByLikeDESC(Long userId, int limit, int offset) {
        String sql = """
                SELECT *
                FROM posts
                WHERE user_id = ?
                ORDER BY (SELECT count(post_id) FROM users_posts WHERE post_id = posts.id) DESC, posts.id
                LIMIT ? OFFSET ?
                """;
        return jdbcTemplate.query(sql, rowMapper, userId, limit, offset);
    }

}

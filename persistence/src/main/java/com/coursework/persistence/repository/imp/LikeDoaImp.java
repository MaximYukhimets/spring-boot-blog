package com.coursework.persistence.repository.imp;

import com.coursework.persistence.repository.LikeDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LikeDoaImp implements LikeDao {

    private final JdbcTemplate jdbcTemplate;

    public LikeDoaImp(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isUserAlreadyLikePost(Long user_id, Long post_id) {
        String sql = """
                SELECT count(*)
                FROM users_posts
                WHERE user_id = ?
                AND
                post_id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, Integer.class, user_id, post_id) == 1;
    }

    @Override
    public List<Long> getAllUserIdsWhoLikedPost(Long id) {
        String sql = """
                SELECT user_id
                FROM users_posts
                WHERE post_id = ?;
                """;
        return jdbcTemplate.queryForList(sql, Long.TYPE, id);
    }

    @Override
    public List<Long> getAllPostIdsLikedByUser(Long id) {
        String sql = """
                SELECT post_id
                FROM users_posts
                WHERE user_id = ?;
                """;
        return jdbcTemplate.queryForList(sql, Long.TYPE, id);
    }

    @Override
    public int addLike(Long user_id, Long post_id) {
        String sql = """
                INSERT INTO users_posts(user_id, post_id)
                VALUES (?, ?);
                """;
        return jdbcTemplate.update(sql, user_id, post_id);
    }

    @Override
    public int cancelLike(Long user_id, Long post_id) {
        return jdbcTemplate.update("DELETE FROM users_posts WHERE user_id = ? AND post_id = ?", user_id, post_id);
    }

    @Override
    public int clearRowByPostId(Long id) {
        return jdbcTemplate.update("DELETE FROM users_posts WHERE post_id = ?;", id);
    }

    @Override
    public int clearRowByUserId(Long id) {
        return jdbcTemplate.update("DELETE FROM users_posts WHERE user_id = ?;", id);
    }

    @Override
    public Integer getPostLikeNumber(Long id) {
        String sql = """
                SELECT count(*)
                FROM users_posts
                WHERE post_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, Integer.TYPE, id);
    }

}

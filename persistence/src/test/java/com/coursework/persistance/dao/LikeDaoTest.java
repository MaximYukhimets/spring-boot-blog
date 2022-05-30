package com.coursework.persistance.dao;


import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.LikeDao;
import com.coursework.persistence.repository.PostDao;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.repository.imp.LikeDoaImp;
import com.coursework.persistence.repository.imp.PostDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@DataJdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {PostDaoImp.class, UserDaoImp.class, LikeDoaImp.class})
@Sql(scripts = {"classpath:scripts/tables/users_schema.sql",
                "classpath:scripts/tables/posts_schema.sql",
                "classpath:scripts/tables/likes_schema.sql"})

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikeDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PostDao postDao;

    @Autowired
    UserDao userDao;

    @Autowired
    LikeDao likeDao;

    @Test
    @DisplayName("Return true if user already liked post")
    void is_user_already_liked_post() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost);
        likeDao.addLike(testUser.getId(), testPost.getId());

        assertTrue(likeDao.isUserAlreadyLikePost(testUser.getId(), testPost.getId()));
    }

    @Test
    @DisplayName("Return all users who liked post")
    void get_all_users_who_liked_post() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost);

    }

    @Test
    @DisplayName("Return the ids of all users who liked the post")
    void get_all_user_ids_who_liked_post() {
        User testUser1 = new User(1, "test_user_t",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        User testUser2 = new User(2, "test_user_t2",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@12345.com", LocalDateTime.now(), true);

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser1);
        userDao.save(testUser2);

        postDao.save(testPost1);
        postDao.save(testPost2);

        likeDao.addLike(testUser1.getId(), testPost1.getId());
        likeDao.addLike(testUser1.getId(), testPost2.getId());
        likeDao.addLike(testUser2.getId(), testPost2.getId());

        assertThat(likeDao.getAllUserIdsWhoLikedPost(testPost2.getId())).isEqualTo(
                List.of(
                        testUser1.getId(),
                        testUser2.getId()
                ));
    }

    @Test
    @DisplayName("Return all post ids liked by user")
    void get_all_post_ids_liked_by_user() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        User testUser2 = new User(2, "test_user_2",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@12345.com", LocalDateTime.now(), true);

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);
        Post testPost3 = new Post(3, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser1);
        userDao.save(testUser2);

        postDao.save(testPost1);
        postDao.save(testPost2);
        postDao.save(testPost3);

        likeDao.addLike(testUser1.getId(), testPost1.getId());
        likeDao.addLike(testUser1.getId(), testPost2.getId());
        likeDao.addLike(testUser2.getId(), testPost3.getId());

        assertThat(likeDao.getAllPostIdsLikedByUser(testUser1.getId())).isEqualTo(
                List.of(
                        testPost1.getId(),
                        testPost2.getId()
                ));
    }

    @Test
    @DisplayName("Cancel like")
    void removes_like() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser1);
        postDao.save(testPost1);

        likeDao.addLike(testUser1.getId(), testPost1.getId());
        assertThat(likeDao.cancelLike(testUser1.getId(), testPost1.getId())).isEqualTo(1);

    }

    @Test
    @DisplayName("Removes all likes from a post")
    void remove_all_like_from_post() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        User testUser2 = new User(2, "test_user_2",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@12345.com", LocalDateTime.now(), true);


        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser1);
        userDao.save(testUser2);
        postDao.save(testPost1);

        likeDao.addLike(testUser1.getId(), testPost1.getId());
        likeDao.addLike(testUser2.getId(), testPost1.getId());

        assertThat(likeDao.getPostLikeNumber(testPost1.getId())).isEqualTo(2);

        likeDao.clearRowByPostId(testPost1.getId());

        assertThat(likeDao.getPostLikeNumber(testPost1.getId())).isEqualTo(0);

    }

    @Test
    @DisplayName("Remove all likes made by user")
    void remove_all_likes_made_by_user() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser1);

        postDao.save(testPost1);
        postDao.save(testPost2);

        likeDao.addLike(testUser1.getId(), testPost1.getId());
        likeDao.addLike(testUser1.getId(), testPost2.getId());

        assertThat(likeDao.getAllPostIdsLikedByUser(testUser1.getId()).size()).isEqualTo(2);

        likeDao.clearRowByUserId(testUser1.getId());

        assertThat(likeDao.getAllPostIdsLikedByUser(testUser1.getId()).size()).isEqualTo(0);

    }

}

package com.coursework.persistance.dao;

import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.CommentDao;
import com.coursework.persistence.repository.PostDao;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.repository.imp.CommentDaoImp;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;


@RunWith(SpringRunner.class)
@DataJdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {PostDaoImp.class, UserDaoImp.class, CommentDaoImp.class})
@Sql(scripts = {"classpath:scripts/tables/users_schema.sql",
                "classpath:scripts/tables/posts_schema.sql",
                "classpath:scripts/tables/comments_schema.sql"})

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PostDao postDao;

    @Autowired
    UserDao userDao;

    @Autowired
    CommentDao commentDao;

    @Test
    @DisplayName("Return all comments from the table")
    void return_all_comments_from_table() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);

        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.findAll()).isEqualTo(
                List.of(
                        testComment1,
                        testComment2
                ));
    }

    @Test
    @DisplayName("Return comment by id")
    void return_comment_by_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment);

        assertThat(commentDao.findById(1L)).get().isEqualTo(testComment);
    }

    @Test
    @DisplayName("Return comment by id")
    void return_comment_by_creation_date() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(),
                LocalDateTime.parse("2019-03-04 11:30:40", formatter), testPost.getId());

        Comment testComment2 = new Comment(2, "body", testUser.getId(),
                LocalDateTime.parse("2020-03-04 11:30:40", formatter), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        Timestamp value = Timestamp.valueOf(testComment2.getCreationDate());

        assertThat(commentDao.findByCreationDate(value)).get().isEqualTo(testComment2);
    }

    @Test
    @DisplayName("Nothing will happen if you try to take a comment by a non-existent id")
    void nothing_happened_when_trying_to_take_comment_by_non_existent_id() {
        assertThatCode(() -> commentDao.findById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Return all comments written by the user")
    void return_all_comments_written_by_user() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.findByUserId(testUser.getId())).isEqualTo(
                List.of(
                        testComment1,
                        testComment2
                ));
    }

    @Test
    @DisplayName("Return all comments associated with the post")
    void return_all_comments_associated_with_post() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.findByPostId(testPost.getId())).isEqualTo(
                List.of(
                        testComment1,
                        testComment2
                ));
    }

    @Test
    @DisplayName("Delete comment by id")
    void delete_comment_by_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.getNumberOfEntity()).isEqualTo(2);

        commentDao.deleteById(testComment1.getId());

        assertThat(commentDao.getNumberOfEntity()).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete all user comments")
    @DirtiesContext
    void delete_all_user_comments() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.getNumberOfEntityByUserId(testUser.getId())).isEqualTo(2);

        commentDao.deleteByUserId(testUser.getId());

        assertThat(commentDao.getNumberOfEntityByUserId(testUser.getId())).isEqualTo(0);
    }

    @Test
    @DisplayName("Delete all post comments")
    @DirtiesContext
    void delete_all_post_comments() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        Comment testComment1 = new Comment(1, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());
        Comment testComment2 = new Comment(2, "body", testUser.getId(), LocalDateTime.now(), testPost.getId());

        userDao.save(testUser);
        postDao.save(testPost);
        commentDao.save(testComment1);
        commentDao.save(testComment2);

        assertThat(commentDao.getNumberOfEntityByPostId(testPost.getId())).isEqualTo(2);

        commentDao.deleteByPostId(testPost.getId());

        assertThat(commentDao.getNumberOfEntityByPostId(testPost.getId())).isEqualTo(0);
    }
}

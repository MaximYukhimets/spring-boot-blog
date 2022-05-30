package com.coursework.persistance.dao;

import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.PostDao;
import com.coursework.persistence.repository.UserDao;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@DataJdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {PostDaoImp.class, UserDaoImp.class})
@Sql(scripts = {"classpath:/db/migration/tables/V20220429230353__Create_users_table.sql",
        "classpath:/db/migration/tables/V20220429230658__Create_posts_table.sql"})

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PostDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PostDao postDao;

    @Autowired
    UserDao userDao;

    @Test
    @DisplayName("Returns all posts from the table")
    void return_all_posts_from_table() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);
        postDao.save(testPost2);

        assertThat(postDao.findAll()).isEqualTo(
                List.of(
                        testPost1,
                        testPost2
                ));
    }

    @Test
    @DisplayName("Return post by id")
    void return_post_by_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);

        assertThat(postDao.findById(1L)).get().isEqualTo(testPost1);
    }

    @Test
    @DisplayName("Return post by user id")
    void return_post_by_user_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);

        assertThat(postDao.findByUserId(1L)).isEqualTo(List.of(testPost1));
    }

    @Test
    @DisplayName("Update post value")
    void update_user_value() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);

        Post postBeforeUpdate = postDao.findById(1L).get();

        testPost1.setTitle("new title");

        assertThat(postDao.update(testPost1)).isEqualTo(1);

        assertNotSame(postDao.findById(1L).get().getTitle(), postBeforeUpdate.getTitle());
    }

    @Test
    @DisplayName("Nothing happened when trying to delete post by not existing id")
    void nothing_happened_when_trying_to_delete_post_by_not_existing_id() {
        assertThatCode(() -> postDao.deleteById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Delete post by user id")
    @DirtiesContext
    void delete_post_by_user_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);

        assertThat(postDao.getNumberOfEntity()).isEqualTo(1);

        postDao.deleteByUserId(testUser.getId());

        assertThat(postDao.getNumberOfEntity()).isZero();
    }

    @Test
    @DisplayName("Return the number of post associated with the user")
    @DirtiesContext
    void number_of_post_associated_with_user_id() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);

        userDao.save(testUser);
        postDao.save(testPost1);
        postDao.save(testPost2);

        assertThat(postDao.getNumberOfEntityByUserId(testUser.getId())).isEqualTo(2);

    }

    @Test
    @DisplayName("Take part of posts sorted by date with limit and offset")
    @DirtiesContext
    void take_part_posts_values_ordered_by_date_with_limit_and_offset() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.parse("2019-03-04 11:30:40", formatter), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.parse("2020-03-04 11:30:40", formatter), 1);
        Post testPost3 = new Post(3, "title", "body", LocalDateTime.parse("2021-03-04 11:30:40", formatter), 1);
        Post testPost4 = new Post(4, "title", "body", LocalDateTime.parse("2022-03-04 11:30:40", formatter), 1);

        userDao.save(testUser);
        postDao.save(testPost1);
        postDao.save(testPost2);
        postDao.save(testPost3);
        postDao.save(testPost4);

        assertThat(postDao.findAllWithLimitAndOffsetOrderByDataDESC(2, 1)).isEqualTo(
                List.of(testPost3, testPost2)
        );

    }

    @Test
    @DisplayName("Take part of posts related to user sorted by date with limit and offset")
    @DirtiesContext
    void take_part_posts_values_related_to_user_ordered_by_date_with_limit_and_offset() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        User testUser2 = new User(2, "test_user_2",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@12345.com", LocalDateTime.now(), true);

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.parse("2019-03-04 11:30:40", formatter), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.parse("2020-03-04 11:30:40", formatter), 1);
        Post testPost3 = new Post(3, "title", "body", LocalDateTime.parse("2021-03-04 11:30:40", formatter), 2);
        Post testPost4 = new Post(4, "title", "body", LocalDateTime.parse("2022-03-04 11:30:40", formatter), 2);

        userDao.save(testUser1);
        userDao.save(testUser2);
        postDao.save(testPost1);
        postDao.save(testPost2);
        postDao.save(testPost3);
        postDao.save(testPost4);

        assertThat(postDao.findByUserIdWithLimitAndOffsetOrderByDataDESC(testUser1.getId(),2, 0)).isEqualTo(
                List.of(testPost2, testPost1)
        );

    }
}

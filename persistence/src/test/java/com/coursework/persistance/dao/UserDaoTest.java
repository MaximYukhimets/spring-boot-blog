package com.coursework.persistance.dao;

import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.repository.imp.UserDaoImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;



@RunWith(SpringRunner.class)
@DataJdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {UserDaoImp.class})
@Sql(scripts = {"classpath:scripts/tables/users_schema.sql"})

public class UserDaoTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserDao userDao;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users");
    }

    @Test
    @DisplayName("Returns all users from the table")
    @DirtiesContext
    void return_all_users_from_table() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);
        User testUser2 = new User(2, "test_user_2",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@12345.com", LocalDateTime.now(), true);

        userDao.save(testUser1);
        userDao.save(testUser2);

        var userEntities = userDao.findAll();
        assertThat(userEntities).isEqualTo(
                List.of(
                        testUser1,
                        testUser2
                ));
    }

    @Test
    @DisplayName("Return user by id")
    @DirtiesContext
    void return_user_by_id() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);

        assertThat(userDao.findById(1L)).get().isEqualTo(testUser1);
    }

    @Test
    @DisplayName("Nothing will happen if you try to take a user by a non-existent id")
    void nothing_happened_when_trying_to_take_user_by_non_existent_id() {
        assertThatCode(() -> userDao.findById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Return user from table by username")
    @DirtiesContext
    void return_user_by_username() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);

        assertThat(userDao.findByUserName("test_user_1")).get().isEqualTo(testUser1);
    }

    @Test
    @DisplayName("Return user from table by email")
    @DirtiesContext
    void return_user_by_email() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);

        assertThat(userDao.findByEmail("mail@1234.com")).get().isEqualTo(testUser1);
    }

    @Test
    @DisplayName("Successfully updates user values")
    @DirtiesContext
    void successfully_updates_user_values() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);
        testUser1.setUsername("another_username");

        assertThat(userDao.update(testUser1)).isEqualTo(1);
    }

    @Test
    @DisplayName("Successfully updates part of the user values")
    @DirtiesContext
    void successfully_updates_part_of_user_values() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        EditUserDto testUserPart = new EditUserDto(1, "Hunter", "Thompson",
                "anotherMail@1234.com", "about");

        userDao.save(testUser1);

        assertThat(userDao.update(testUserPart)).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete user by id")
    @DirtiesContext
    void delete_user_by_id() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);

        assertThat(userDao.getNumberOfEntity()).isEqualTo(1);

        userDao.deleteById(testUser1.getId());

        assertThat(userDao.getNumberOfEntity()).isZero();
    }

    @Test
    @DisplayName("Nothing happened when trying to delete not existing user")
    void nothing_happened_when_trying_to_delete_not_existing_user() {
        assertThatCode(() -> userDao.deleteById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Delete user by id")
    void delete_user_by_username() {
        User testUser1 = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        userDao.save(testUser1);

        assertThat(userDao.getNumberOfEntity()).isEqualTo(1);

        userDao.deleteByUsername(testUser1.getUsername());

        assertThat(userDao.getNumberOfEntity()).isZero();
    }

}

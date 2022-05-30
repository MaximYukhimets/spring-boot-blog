package com.coursework.persistance.dao;

import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.repository.imp.RoleDaoImp;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@DataJdbcTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ContextConfiguration(classes = {UserDaoImp.class, RoleDaoImp.class})
@Sql(scripts = {"classpath:/db/migration/tables/V20220429230353__Create_users_table.sql",
        "classpath:/db/migration/tables/V20220430145105__Create_roles_table.sql",
        "classpath:/db/migration/tables/V20220430153829__Create_users_roles_table.sql"})

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoleDaoTest {


    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Test
    @DisplayName("Return all roles")
    void return_all_roles() {
        Role testRole1 = new Role(1, "TEST_ROLE_1");
        Role testRole2 = new Role(2, "TEST_ROLE_2");

        roleDao.save(testRole1);
        roleDao.save(testRole2);

        assertThat(roleDao.getAllRole()).isEqualTo(
                Set.of(
                        testRole1,
                        testRole2
                ));
    }

    @Test
    @DisplayName("Return role by id")
    void return_role_by_id() {
        Role testRole = new Role(1, "TEST_ROLE");

        roleDao.save(testRole);

        assertThat(roleDao.getRoleById(1L)).get().isEqualTo(testRole);
    }

    @Test
    @DisplayName("Return role by role name")
    void return_role_by_role_name() {
        Role testRole = new Role(1, "TEST_ROLE");

        roleDao.save(testRole);

        assertThat(roleDao.getRoleByRoleName(testRole.getName())).get().isEqualTo(testRole);
    }

    @Test
    @DisplayName("Return all user roles")
    void return_all_user_roles() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        Role testRole1 = new Role(1, "TEST_ROLE_1");
        Role testRole2 = new Role(2, "TEST_ROLE_2");

        userDao.save(testUser);
        roleDao.save(testRole1);
        roleDao.save(testRole2);

        roleDao.saveUserRole(testUser.getId(), testRole1.getId());
        roleDao.saveUserRole(testUser.getId(), testRole2.getId());

        assertThat(roleDao.getAllUserRoleByUserId(testUser.getId())).isEqualTo(
                Set.of(
                        testRole1,
                        testRole2
                ));
    }

    @Test
    @DisplayName("Delete user roles")
    void delete_user_roles() {
        User testUser = new User(1, "test_user_1",  "Hunter", "Thompson",
                "$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi",
                "mail@1234.com", LocalDateTime.now(), true);

        Role testRole1 = new Role(1, "TEST_ROLE_1");

        userDao.save(testUser);
        roleDao.save(testRole1);

        roleDao.saveUserRole(testUser.getId(), testRole1.getId());

        assertThat(roleDao.getAllUserRoleByUserId(testUser.getId()).size()).isEqualTo(1);

        roleDao.deleteUserRole(testUser.getId(), testRole1.getId());

        assertThat(roleDao.getAllUserRoleByUserId(testUser.getId()).size()).isEqualTo(0);
    }

}

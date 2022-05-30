package com.coursework.persistance.services;


import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.services.imp.RoleServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleDao roleDao;

    private RoleServiceImp roleService;

    @BeforeEach
    void setUp() {
            roleService = new RoleServiceImp(roleDao);
    }

    @Test
    @DisplayName("Get all role by user id")
    void get_all_role_by_user_id() {
        Role testRole = new Role(1, "TEST_ROLE");

        when(roleDao.getAllUserRoleByUserId(1L))
                .thenReturn(
                        Set.of(testRole)
                );

        assertThat(roleService.getAllUserRoleByUserId(1L)).isEqualTo(
                Set.of(testRole)
        );
    }

    @Test
    @DisplayName("Add user role")
    void add_user_role() {
        User testUser = new User(1, "username", "email");
        Role testRole = new Role(1, "TEST_ROLE");

        when(roleDao.getRoleByRoleName("TEST_ROLE")).thenReturn(Optional.of(testRole));
        when(roleDao.saveUserRole(anyLong(), anyLong())).thenReturn(1);

        roleService.addUserRole(testUser, testRole);

        verify(roleDao).saveUserRole(anyLong(), anyLong());

    }

    @Test
    @DisplayName("Add user role failed")
    void add_user_role_failed() {
        User testUser = new User(1, "username", "email");
        Role testRole = new Role(1, "TEST_ROLE");

        when(roleDao.getRoleByRoleName("TEST_ROLE")).thenReturn(Optional.empty());

        assertThatCode(() -> roleService.addUserRole(testUser, testRole))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("Delete user role")
    void delete_user_role() {
        User testUser = new User(1, "username", "email");
        Role testRole = new Role(1, "TEST_ROLE");

        when(roleDao.getRoleByRoleName("TEST_ROLE")).thenReturn(Optional.of(testRole));
        when(roleDao.deleteUserRole(anyLong(), anyLong())).thenReturn(1);

        roleService.deleteUserRole(testUser, testRole);

        verify(roleDao).deleteUserRole(anyLong(), anyLong());

    }

    @Test
    @DisplayName("Add user role failed")
    void delete_user_role_failed() {
        User testUser = new User(1, "username", "email");
        Role testRole = new Role(1, "TEST_ROLE");

        when(roleDao.getRoleByRoleName("TEST_ROLE")).thenReturn(Optional.empty());

        assertThatCode(() -> roleService.deleteUserRole(testUser, testRole))
                .isInstanceOf(NotFoundException.class);

    }
}

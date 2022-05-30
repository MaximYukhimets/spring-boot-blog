package com.coursework.persistence.repository;

import com.coursework.domain.entity.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleDao {

    Optional<Role> getRoleByRoleName(String role);

    Optional<Role> getRoleById(Long id);

    Set<Role> getAllRole();

    Set<Role> getAllUserRoleByUserId(Long id);

    int save(Role role);

    int saveUserRole(Long userId, Long roleId);

    int deleteUserRole(Long userId, Long roleId);
}

package com.coursework.persistence.services;

import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;

import java.util.Set;

public interface RoleService {

    Set<Role> getAllUserRoleByUserId(Long id);

    void addUserRole(User user, Role role);

    void deleteUserRole(User user, Role role);
}

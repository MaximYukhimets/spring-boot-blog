package com.coursework.persistence.services.imp;

import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RoleServiceImp implements RoleService {

    private final RoleDao roleDao;

    Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public RoleServiceImp(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Set<Role> getAllUserRoleByUserId(Long id) {
        return roleDao.getAllUserRoleByUserId(id);
    }

    @Override
    public void addUserRole(User user, Role role) {
        Role roleWithId = roleDao.getRoleByRoleName(role.getName()).orElseThrow(
                () -> new NotFoundException(String.format("Role - %s not found", role.getName()))
        );

        if (roleDao.saveUserRole(user.getId(), roleWithId.getId()) == 1) {
            logger.info(String.format("User : %s got a role - %s", user.getUsername(), role.getName()));
        } else {
            logger.warn(String.format("Failed to set user - %s , role - %s", user.getUsername(), role.getName()));
        }
    }

    @Override
    public void deleteUserRole(User user, Role role) {
        Role roleWithId = roleDao.getRoleByRoleName(role.getName()).orElseThrow(
                () -> new NotFoundException(String.format("Role - %s not found", role.getName()))
        );

        if (roleDao.deleteUserRole(user.getId(), roleWithId.getId()) == 1) {
            logger.info(String.format("User - %s had role - %s removed", user.getUsername(), role.getName()));
        } else {
            logger.warn(String.format("Failed to remove role - %s from user - %s", user.getUsername(), role.getName()));
        }
    }
}

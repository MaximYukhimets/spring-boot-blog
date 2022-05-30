package com.coursework.web.security;

import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationUserService implements UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public AuthenticationUserService(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUserName(username).orElseThrow(
                () -> new NotFoundException("User not found by username - " + username));
        Set<Role> userRoles = roleDao.getAllUserRoleByUserId(user.getId());
        user.setRoles(userRoles);

        return AuthenticationUser.fromUser(user);
    }
}

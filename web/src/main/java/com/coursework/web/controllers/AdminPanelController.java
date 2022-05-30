package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.dto.UserForAdminPanelDto;
import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.RoleService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.security.AuthenticationUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
public class AdminPanelController {

    private final UserService userService;
    private final PostService postService;
    private final RoleService roleService;
    private final CommentService commentService;
    private final EntityMapper mapper;

    private static final Predicate<AuthenticationUser> isUserAuthAndAdminOrSuperAdmin = (auth) -> auth != null && auth.getAuthorities()
            .stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    public AdminPanelController(UserService userService, PostService postService, RoleService roleService, CommentService commentService, EntityMapper mapper) {
        this.userService = userService;
        this.postService = postService;
        this.roleService = roleService;
        this.commentService = commentService;
        this.mapper = mapper;
    }


    @GetMapping("/admin-panel")
    public String getAdminPanel(@AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (!isUserAuthAndAdminOrSuperAdmin.test(authenticationUser)) {
            return "redirect:/";
        }

        return "/admin";
    }

    @ResponseBody
    @GetMapping("/get-all-users")
    public List<UserForAdminPanelDto> getAllUsers(@AuthenticationPrincipal AuthenticationUser authenticationUser) {
        List<UserForAdminPanelDto> usersDto = new ArrayList<>();

        if (authenticationUser != null) {
            List<User> users = userService.findAll();
            for (User user : users) {
                UserForAdminPanelDto userDto = mapper.toUserForAdminPanelDto(user);
                List<Role> roles = roleService.getAllUserRoleByUserId(user.getId()).stream()
                        .sorted(Comparator.comparing(Role::getId))
                        .collect(Collectors.toList());
                userDto.setRoles(roles);
                usersDto.add(userDto);
            }
        }

        return usersDto;
    }

    @ResponseBody
    @GetMapping("/get-content")
    public Map<String, Integer> getContent(@AuthenticationPrincipal AuthenticationUser authenticationUser) {
        Map<String, Integer> content = new HashMap<>();

        if (authenticationUser != null) {
            content.put("postCount", postService.getNumberOfEntity());
            content.put("commentCount", commentService.getNumberOfEntity());
        }

        return content;
    }

}

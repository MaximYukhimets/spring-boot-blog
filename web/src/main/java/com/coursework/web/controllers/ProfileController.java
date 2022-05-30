package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.dto.PostDto;
import com.coursework.domain.dto.UserDto;
import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.persistence.Pager;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.RoleService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.PostSortingOrderConfig;
import com.coursework.web.security.AuthenticationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class ProfileController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final RoleService roleService;
    private final EntityMapper mapper;

    private final PostSortingOrderConfig postSortingOrderConfig;

    private static final int PAGE_SIZE = 2;
    private static final Predicate<AuthenticationUser> isUserAuthAndAdminOrSuperAdmin = (auth) -> auth != null &&
            auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") ||
                    r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    private static final Predicate<AuthenticationUser> isUserAuthAndSuperAdmin = (auth) -> auth != null &&
            auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    @Autowired
    public ProfileController(UserService userService, PostService postService, CommentService commentService,
                             RoleService roleService, EntityMapper mapper, PostSortingOrderConfig postSortingOrderConfig) {
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
        this.roleService = roleService;
        this.mapper = mapper;
        this.postSortingOrderConfig = postSortingOrderConfig;
    }

    @GetMapping(value = "/{username}")
    public String userBlog(@PathVariable String username,
                           @RequestParam(defaultValue = "1") int page,
                           Model model,
                           @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        User user = userService.findByUsername(username);
        List<Role> roles = roleService.getAllUserRoleByUserId(user.getId()).stream()
                .sorted(Comparator.comparing(Role::getId))
                .collect(Collectors.toList());

        UserDto userDto = mapper.toDto(user);
        userDto.setRoles(roles);

        Pager pager = new Pager(postService.getNumberOfEntityByUserId(user.getId()), PAGE_SIZE);
        List<PostDto> pagePost;

        if (postSortingOrderConfig.isSortingByLikeOn()) {
            pagePost = postService.getPageByUserIdWithLimitAndOffsetOrderByLikeDESC(user.getId(),
                            PAGE_SIZE, pager.getOffset(page))
                    .stream().map(mapper::toDto).collect(Collectors.toList());
        } else {
            pagePost = postService.getPageByUserIdWithLimitAndOffsetOrderByDataDESC(user.getId(),
                            PAGE_SIZE, pager.getOffset(page))
                    .stream().map(mapper::toDto).collect(Collectors.toList());
        }

        for (PostDto postDto : pagePost) {
            postDto.setUser(mapper.toUserForPostDto(user));
            postDto.setLikeCounter(postService.getPostLikeNumber(postDto.getId()));
            postDto.setCommentCounter(commentService.getNumberOfPostCommentByPostId(postDto.getId()));
            if (authenticationUser != null) {
                postDto.setIsUserAlreadyLikePost(postService.isUserAlreadyLikePost(
                        postDto.getId(), authenticationUser.getId()));
            }
        }

        UnaryOperator<String> reducer = s -> String.join(" ", Arrays.asList(s.split(" ")).subList(0, 70));

        model.addAttribute("user", userDto);
        model.addAttribute("isAdminOrSuperAdmin", userDto.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN")));
        model.addAttribute("isAdmin", userDto.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN") ));
        model.addAttribute("posts", pagePost);
        model.addAttribute("reducer", reducer);
        model.addAttribute("pager", pager);

        return "/profile";
    }

    @PostMapping(value = "/{username}/upload")
    public String uploadImage(@PathVariable String username, @RequestParam("file") MultipartFile file, Model model,
                         @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null || !authenticationUser.getUsername().equals(username) || file.isEmpty()) {
            return "redirect:/user/" + username;
        }

        userService.updateImageByUsername(file, username);

        return "redirect:/user/" + username;
    }

    @GetMapping(value = "/{username}/edit")
    public String editProfile(@PathVariable String username, Model model,
                       @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null || !authenticationUser.getUsername().equals(username)) {
            return "redirect:/user/" + username;
        }

        User user = userService.findByUsername(username);

        model.addAttribute("user", mapper.toEditUserDto(user));
        return "/editProfile";
    }

    @PostMapping("/{username}/edit")
    public String editProfile(@PathVariable String username,
                       @Valid @ModelAttribute("user") EditUserDto editUserDto,
                       BindingResult bindingResult,
                       @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null || !authenticationUser.getUsername().equals(username)) {
            return "redirect:/user/" + editUserDto.getUsername();
        }

        userService.editUserDtoValidation(editUserDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/editProfile";
        }

        userService.update(editUserDto);

        return "redirect:/user/" + editUserDto.getUsername();

    }

    @PostMapping("/{username}/ban")
    public String banUser(@PathVariable String username,
                      @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (!isUserAuthAndAdminOrSuperAdmin.test(authenticationUser)) {
            return "redirect:/user/" + username;
        }

        User user = userService.findByUsername(username);
        Set<Role> roles = roleService.getAllUserRoleByUserId(user.getId());

        if (user.isActive() && roles.stream().noneMatch(
                r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"))) {
            user.setActive(false);
            userService.update(user);
        }

        return "redirect:/user/" + username;
    }

    @PostMapping("/{username}/unban")
    public String unbanUser(@PathVariable String username,
                        @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (!isUserAuthAndAdminOrSuperAdmin.test(authenticationUser)) {
            return "redirect:/user/" + username;
        }

        User user = userService.findByUsername(username);
        user.setActive(true);

        userService.update(user);

        return "redirect:/user/" + username;
    }

    @PostMapping("/{username}/make-admin")
    public String makeAdmin(@PathVariable String username,
                            @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (!isUserAuthAndSuperAdmin.test(authenticationUser)) {
            return "redirect:/user/" + username;
        }

        User user = userService.findByUsername(username);
        Set<Role> roles = roleService.getAllUserRoleByUserId(user.getId());

        if (roles.stream().noneMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN")) &&
                user.isActive()) {
            roleService.addUserRole(user, new Role("ADMIN"));
        }

        return "redirect:/user/" + username;
    }

    @PostMapping("/{username}/delete-admin")
    public String deleteAdmin(@PathVariable String username,
                              @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (!isUserAuthAndSuperAdmin.test(authenticationUser)) {
            return "redirect:/user/" + username;
        }

        User user = userService.findByUsername(username);
        Set<Role> roles = roleService.getAllUserRoleByUserId(user.getId());

        if (roles.stream().anyMatch(r -> r.getName().equals("ADMIN"))) {
            roleService.deleteUserRole(user, new Role("ADMIN"));
        }

        return "redirect:/user/" + username;
    }

}


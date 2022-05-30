package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.CommentDto;
import com.coursework.domain.dto.UserForCommentDto;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.User;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.security.AuthenticationUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
public class CommentController {

    private final UserService userService;
    private final CommentService commentService;
    private final EntityMapper mapper;

    private static final Predicate<AuthenticationUser> isUserAuthAndAdminOrSuperAdmin = (auth) -> auth != null && auth.getAuthorities()
            .stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    private static final Predicate<AuthenticationUser> isUserAuthAndSuperAdmin = (auth) -> auth != null &&
            auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    public CommentController(UserService userService, CommentService commentService, EntityMapper mapper) {
        this.userService = userService;
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @GetMapping("/comments")
    public List<CommentDto> getPostComments(@RequestParam(name = "postId") long postId) {

        List<CommentDto> comments = commentService.getAllPostCommentsByPostId(postId)
                .stream().map(mapper::toDto).collect(Collectors.toList());

        for (CommentDto comment : comments) {
            User user = userService.findById(comment.getUserId());
            UserForCommentDto userDto = mapper.toUserForCommentDto(user);
            comment.setUser(userDto);
        }

        return comments;
    }

    @PostMapping("/add-comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                              @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null) {
            return null;
        }

        Comment comment = commentService.save(commentDto, authenticationUser.getId());
        CommentDto returnDto = mapper.toDto(comment);
        User user = userService.findById(comment.getUserId());
        UserForCommentDto userDto = mapper.toUserForCommentDto(user);
        returnDto.setUser(userDto);

        return returnDto;
    }

    @DeleteMapping("/delete-comment/{id}")
    public void deleteComment(@PathVariable long id,
                              @AuthenticationPrincipal AuthenticationUser authenticationUser) {
        Comment comment = commentService.getById(id);

        if (authenticationUser != null) {
            if (comment.getUserId() == authenticationUser.getId() || isUserAuthAndAdminOrSuperAdmin.test(authenticationUser)) {
                commentService.deleteById(id);
            }
        }

    }

    @GetMapping("/current-user")
    public Map<String, Integer> getCurrentUser(@AuthenticationPrincipal AuthenticationUser authenticationUser) {
        Map<String, Integer> currentUser = new HashMap<>();

        if (authenticationUser != null) {
            currentUser.put("id", (int) authenticationUser.getId());
            currentUser.put("isUserAdminOrSuperAdmin", isUserAuthAndAdminOrSuperAdmin.test(authenticationUser) ? 1 : 0);
            currentUser.put("isUserSuperAdmin", isUserAuthAndSuperAdmin.test(authenticationUser) ? 1 : 0);
        }

        return currentUser;
    }

}

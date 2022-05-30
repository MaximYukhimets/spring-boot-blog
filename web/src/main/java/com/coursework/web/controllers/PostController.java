package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.dto.PostDto;
import com.coursework.domain.dto.UserForPostDto;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.security.AuthenticationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.function.Predicate;

@Controller
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final EntityMapper mapper;

    private static final Predicate<AuthenticationUser> isUserAuthAndAdminOrSuperAdmin = (auth) -> auth != null && auth.getAuthorities()
            .stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_SUPER_ADMIN"));

    @Autowired
    public PostController(PostService postService, UserService userService, CommentService commentService, EntityMapper mapper) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.mapper = mapper;
    }

    @GetMapping(value = "/new-post")
    public String newPost(Model model, @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null) {
            return "redirect:/";
        }

        model.addAttribute("post", new PostCreationDto());
        return "/postForm";
    }

    @PostMapping(value = "/new-post")
    public String createPost(@Valid @ModelAttribute("post") PostCreationDto postCreationDto,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        if (authenticationUser == null) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "/postForm";
        }

        postService.save(postCreationDto, authenticationUser.getId());

        return "redirect:/";
    }

    @GetMapping(value = "/post/{id}")
    public String showPost(@PathVariable long id, Model model,
                           @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        Post post = postService.getById(id);
        User user = userService.findById(post.getUserId());
        PostDto postDto = mapper.toDto(post);
        UserForPostDto userForPostDto = mapper.toUserForPostDto(user);
        postDto.setUser(userForPostDto);
        postDto.setLikeCounter(postService.getPostLikeNumber(postDto.getId()));
        postDto.setCommentCounter(commentService.getNumberOfPostCommentByPostId(postDto.getId()));

        if (post.getImage() != null) {
            postDto.setImage64(Base64.getEncoder().encodeToString(post.getImage()));
        }

        if (authenticationUser != null) {
            model.addAttribute("isAuthorUser", authenticationUser.getId() == post.getUserId());
            postDto.setIsUserAlreadyLikePost(postService.isUserAlreadyLikePost(id, authenticationUser.getId()));
        }

        model.addAttribute("post", postDto);
        model.addAttribute("isBodyEmpty", postDto.getBody().isEmpty());
        model.addAttribute("isUserAdminOrSuperAdmin",
                isUserAuthAndAdminOrSuperAdmin.test(authenticationUser));

        return "/post";
    }

    @GetMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id, Model model,
                           @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        Post post = postService.getById(id);

        if (authenticationUser == null || authenticationUser.getId() != post.getUserId()) {
            return "redirect:/";
        }

        PostCreationDto postCreationDto = mapper.toPostCreationDto(post);
        if (post.getImage() != null) {
            postCreationDto.setImage64(Base64.getEncoder().encodeToString(post.getImage()));
        }

        model.addAttribute("post", postCreationDto);
        return "/editPost";
    }

    @PostMapping(value = "/post/{id}/edit")
    public String editPost(@PathVariable long id,
                           @Valid @ModelAttribute("post") PostCreationDto postCreationDto,
                           BindingResult bindingResult, Model model,
                           @AuthenticationPrincipal AuthenticationUser authenticationUser) throws IOException {

        Post post = postService.getById(id);

        if (authenticationUser == null || authenticationUser.getId() != post.getUserId()) {
            return "redirect:/post/" + id;
        }

        if (bindingResult.hasErrors()) {
            return "/editPost";
        }

        postService.update(post, postCreationDto);

        return "redirect:/post/" + id;
    }

    @PostMapping(value = "/post/{id}/delete")
    public String deletePost(@PathVariable long id,
                             @RequestParam(name = "redirectOn", defaultValue = "") String username,
                             @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        Post post = postService.getById(id);

        if (authenticationUser != null) {
            if (authenticationUser.getId() == post.getUserId() || isUserAuthAndAdminOrSuperAdmin.test(authenticationUser)) {
                postService.deleteById(post.getId());
            }
        }

        if (!username.isEmpty()) {
            return "redirect:/user/" + username;
        }

        return "redirect:/";
    }

    @ResponseBody
    @PostMapping(value = "/post/{id}/like")
    public int likePost(@PathVariable long id,
                           @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        return authenticationUser != null ? postService.likePost(authenticationUser.getId(), id) : 0;

    }

    @ResponseBody
    @PostMapping(value = "/post/{id}/unlike")
    public int unlikePost(@PathVariable long id,
                             @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        return authenticationUser != null ? postService.unlikePost(authenticationUser.getId(), id) : 0;

    }

}

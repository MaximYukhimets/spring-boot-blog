package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostDto;
import com.coursework.persistence.Pager;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.PostSortingOrderConfig;
import com.coursework.web.security.AuthenticationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;


@Controller
public class IndexController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final EntityMapper mapper;

    private final PostSortingOrderConfig postSortingOrderConfig;

    private static final int PAGE_SIZE = 3;

    @Autowired
    public IndexController(PostService postService, UserService userService, CommentService commentService,
                           EntityMapper mapper, PostSortingOrderConfig postSortingOrderConfig) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
        this.mapper = mapper;
        this.postSortingOrderConfig = postSortingOrderConfig;
    }

    @GetMapping( "/")
    public String index(@RequestParam(name = "page", defaultValue = "1") int page,
                        Model model,
                        @AuthenticationPrincipal AuthenticationUser authenticationUser) {

        Pager pager = new Pager(postService.getNumberOfEntity(), PAGE_SIZE);

        List<PostDto> pagePost;
        if (postSortingOrderConfig.isSortingByLikeOn()) {
            pagePost = postService.getPageWithLimitAndOffsetOrderByLikeDESC(PAGE_SIZE, pager.getOffset(page))
                    .stream().map(mapper::toDto).collect(Collectors.toList());
        } else {
            pagePost = postService.getPageWithLimitAndOffsetOrderByDataDESC(PAGE_SIZE, pager.getOffset(page))
                    .stream().map(mapper::toDto).collect(Collectors.toList());
        }

        for (PostDto postDto : pagePost) {
            postDto.setUser(mapper.toUserForPostDto(userService.findById(postDto.getUserId())));
            postDto.setLikeCounter(postService.getPostLikeNumber(postDto.getId()));
            postDto.setCommentCounter(commentService.getNumberOfPostCommentByPostId(postDto.getId()));
            if (authenticationUser != null) {
                postDto.setIsUserAlreadyLikePost(postService.isUserAlreadyLikePost(
                        postDto.getId(), authenticationUser.getId()));
            }
        }

        UnaryOperator<String> reducer = s -> String.join(" ", Arrays.asList(s.split(" ")).subList(0, 70));

        model.addAttribute("posts", pagePost);
        model.addAttribute("reducer", reducer);
        model.addAttribute("pager", pager);

        return "/index";
    }

    @ResponseBody
    @PostMapping(value = "/sort-order")
    public boolean isSortingByLikeOn() {

        return postSortingOrderConfig.isSortingByLikeOn();

    }

    @ResponseBody
    @PostMapping(value = "/change-sort-order")
    public void changeSortOrder(boolean isSwitchOn) {

        postSortingOrderConfig.setSortingByLikeOn(isSwitchOn);

    }
}


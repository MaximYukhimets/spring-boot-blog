package com.coursework.domain.dto;


import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDto {

    private long id;

    @NotEmpty(message = "* Title can't be empty")
    @Size(max = 200, message = "* Title is too long")
    private String title;

    @Size(max = 5000, message = "* Post is too long")
    private String body;

    private LocalDateTime creationDate;

    private UserForPostDto user;

    private long userId;

    private int likeCounter;

    private int commentCounter;

    private String image64;

    private boolean IsUserAlreadyLikePost;
}
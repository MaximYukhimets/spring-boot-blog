package com.coursework.domain.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDto {

    private long id;

    @NotEmpty(message = "* Comment body can't be empty")
    private String body;

    private UserForCommentDto user;

    private long userId;

    private LocalDateTime createDate;

    private long postId;

}

package com.coursework.domain.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class PostCreationDto {

    private long id;

    @NotEmpty(message = "* Title can't be empty")
    @NotBlank(message = "* Title can't be blank")
    @Size(max = 200, message = "* Title is too long")
    private String title;

    @Size(max = 5000, message = "* Post is too long")
    private String body;

    private MultipartFile image;

    private String image64;

    public PostCreationDto(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
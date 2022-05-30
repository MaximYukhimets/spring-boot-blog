package com.coursework.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserForCommentDto {

    private long id;

    private String username;

    private String image64;

}

package com.coursework.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class PostSortingOrderConfig {

    private boolean isSortingByLikeOn = false;
}




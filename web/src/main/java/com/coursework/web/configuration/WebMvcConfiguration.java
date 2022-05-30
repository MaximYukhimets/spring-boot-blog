package com.coursework.web.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry
//                .addResourceHandler("/img/**")
//                .addResourceLocations("file://" + "blog/images/");
//                .addResourceLocations("file:/" + System.getProperty("user.dir") + File.separator + "images/");
//        registry
//                .addResourceHandler("/static/css/")
//                .addResourceLocations("classpath:/static/css/");
//    }

}

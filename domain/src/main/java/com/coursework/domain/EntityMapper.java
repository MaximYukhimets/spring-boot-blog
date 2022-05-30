package com.coursework.domain;

import com.coursework.domain.dto.*;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Component
public class EntityMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastSName());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setAbout(user.getAbout());

        Instant instant = user.getRegistrationDate().atZone(ZoneId.systemDefault()).toInstant();
        dto.setRegistrationDate(Date.from(instant));
        dto.setImage64(Base64.getEncoder().encodeToString(user.getImage()));
        dto.setActive(user.isActive());
        return dto;
    }

    public UserForPostDto toUserForPostDto(User user) {
        UserForPostDto dto = new UserForPostDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }

    public EditUserDto toEditUserDto(User user) {
        EditUserDto dto = new EditUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastSName());
        dto.setEmail(user.getEmail());
        dto.setAbout(user.getAbout());
        return dto;
    }

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastSName(dto.getLastName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        return user;
    }

    public PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setCreationDate(post.getCreationDate());
        dto.setUserId(post.getUserId());
        return dto;
    }

    public Post toEntity(PostCreationDto dto) {
        Post post = new Post();
        post.setId(post.getId());
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());
        return post;
    }

    public PostCreationDto toPostCreationDto(Post post) {
        PostCreationDto dto = new PostCreationDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        return dto;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setBody(comment.getBody());
        dto.setUserId(comment.getUserId());
        dto.setCreateDate(comment.getCreationDate());
        dto.setPostId(comment.getPostId());

        return dto;
    }

    public UserForCommentDto toUserForCommentDto(User user) {
        UserForCommentDto userDto = new UserForCommentDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setImage64(Base64.getEncoder().encodeToString(user.getImage()));

        return userDto;
    }

    public Comment toEntity(CommentDto dto) {
        Comment comment = new Comment();
        comment.setBody(dto.getBody());
        comment.setUserId(dto.getUserId());
        comment.setCreationDate(dto.getCreateDate());
        comment.setPostId(dto.getPostId());

        return comment;
    }

    public UserForAdminPanelDto toUserForAdminPanelDto(User user) {
        UserForAdminPanelDto dto = new UserForAdminPanelDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        Instant instant = user.getRegistrationDate().atZone(ZoneId.systemDefault()).toInstant();
        dto.setRegistrationDate(Date.from(instant));
        dto.setActive(user.isActive());
        dto.setImage64(Base64.getEncoder().encodeToString(user.getImage()));

        return dto;
    }
}

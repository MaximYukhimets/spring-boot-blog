package com.coursework.persistence.services;

import com.coursework.domain.dto.CommentDto;
import com.coursework.domain.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment getById(Long id);

    List<Comment> getAllPostCommentsByPostId(Long postId);

    int getNumberOfPostCommentByPostId(Long postId);

    int getNumberOfEntity();

    List<Comment> getAllUserComments(Long userId);

    int getNumberOfUserCommentByUserId(Long userId);

    Comment save(CommentDto commentDto, Long userId);

    void deleteById(Long id);
}

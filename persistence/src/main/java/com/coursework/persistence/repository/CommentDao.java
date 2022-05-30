package com.coursework.persistence.repository;

import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface CommentDao {

    List<Comment> findAll();

    List<Comment> findByUserId(Long id);

    List<Comment> findByPostId(Long id);

    int save(Comment comment);

    int deleteById(Long id);

    int deleteByUserId(Long id);

    int deleteByPostId(Long id);

    Integer getNumberOfEntityByPostId(Long id);

    Integer getNumberOfEntityByUserId(Long id);

    Integer getNumberOfEntity();

    Optional<Comment> findById(Long id);

    Optional<Comment> findByCreationDate(Timestamp date);
}

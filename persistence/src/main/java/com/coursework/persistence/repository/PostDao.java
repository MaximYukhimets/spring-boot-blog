package com.coursework.persistence.repository;

import com.coursework.domain.entity.Post;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    List<Post> findAll();

    Optional<Post> findById(Long id);

    List<Post> findByUserId(Long userId);

    int save(Post post);

    int update(Post post);

    int deleteById(Long id);

    int deleteByUserId(Long id);

    Integer getNumberOfEntity();

    Integer getNumberOfEntityByUserId(Long id);

    List<Post> findAllWithLimitAndOffsetOrderByDataDESC(int limit, int offset);

    List<Post> findByUserIdWithLimitAndOffsetOrderByDataDESC(Long userId, int limit, int offset);

    List<Post> findAllWithLimitAndOffsetOrderByLikeDESC(int limit, int offset);

    List<Post> findByUserIdWithLimitAndOffsetOrderByLikeDESC(Long userId, int limit, int offset);

}


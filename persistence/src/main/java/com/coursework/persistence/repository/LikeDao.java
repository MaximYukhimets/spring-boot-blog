package com.coursework.persistence.repository;

import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;

import java.util.List;

public interface LikeDao {

    boolean isUserAlreadyLikePost(Long userId, Long postId);

    List<Long> getAllUserIdsWhoLikedPost(Long id);

    List<Long> getAllPostIdsLikedByUser(Long id);

    int addLike(Long userId, Long postId);

    int cancelLike(Long userId, Long postId);

    int clearRowByPostId(Long id);

    int clearRowByUserId(Long id);

    Integer getPostLikeNumber(Long id);
}

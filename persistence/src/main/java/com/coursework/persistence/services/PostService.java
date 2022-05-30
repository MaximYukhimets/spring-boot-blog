package com.coursework.persistence.services;

import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.dto.PostDto;
import com.coursework.domain.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post getById(Long id);

    List<Post> getAllOrderedByDate();

    List<Post> getAllByUserId(Long id);

    void save(PostCreationDto postDto, Long user_id);

    void update(Post post, PostCreationDto postCreationDto);

    void deleteById(Long id);

    int getNumberOfEntity();

    int getNumberOfEntityByUserId(Long id);

    List<Post> getPageWithLimitAndOffsetOrderByDataDESC(int pageSize, int offset);

    List<Post> getPageByUserIdWithLimitAndOffsetOrderByDataDESC(Long id, int pageSize, int offset);

    List<Post> getPageWithLimitAndOffsetOrderByLikeDESC(int pageSize, int offset);

    List<Post> getPageByUserIdWithLimitAndOffsetOrderByLikeDESC(Long id, int pageSize, int offset);

    int likePost(Long userId, Long postId);

    int unlikePost(Long userId, Long postId);

    boolean isUserAlreadyLikePost(Long postId, Long userId);

    int getPostLikeNumber(Long postId);

}

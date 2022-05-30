package com.coursework.persistence.services.imp;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.entity.Post;
import com.coursework.domain.exceptions.FileFormatException;
import com.coursework.persistence.repository.CommentDao;
import com.coursework.persistence.repository.LikeDao;
import com.coursework.persistence.repository.PostDao;
import com.coursework.persistence.services.PostService;
import com.coursework.domain.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImp implements PostService {

    private final PostDao postDao;
    private final CommentDao commentDao;
    private final LikeDao likeDao;
    private final EntityMapper mapper;

    Logger logger = LoggerFactory.getLogger(PostServiceImp.class);

    @Autowired
    public PostServiceImp(PostDao postDao, CommentDao commentDao, LikeDao likeDao, EntityMapper mapper) {
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.likeDao = likeDao;
        this.mapper = mapper;
    }

    @Override
    public Post getById(Long id) {
        return postDao.findById(id).orElseThrow(() -> new NotFoundException("Post not found by id - " + id));
    }

    @Override
    public List<Post> getAllByUserId(Long id) {
        return postDao.findByUserId(id);
    }

    @Override
    public List<Post> getAllOrderedByDate() {
        return postDao.findAll().stream()
                .sorted(Comparator.comparing(Post::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void save(PostCreationDto postDto, Long user_id) {
        Post post = mapper.toEntity(postDto);
        post.setCreationDate(LocalDateTime.now());
        post.setUserId(user_id);

        if (!postDto.getImage().isEmpty()) {
            byte[] imageBytes = null;
            try {
                imageBytes = postDto.getImage().getBytes();
            } catch (IOException e) {
                logger.warn("Error when trying to convert MultipartFile to byte array");
            }
            post.setImage(imageBytes);
        }

        if (postDao.save(post) == 1) {
            logger.info("New post has been created");
        }
    }

    @Override
    public void update(Post post, PostCreationDto postCreationDto) {

        post.setTitle(postCreationDto.getTitle());
        post.setBody(postCreationDto.getBody());
        byte[] imageBytes;
        try {
            imageBytes = !postCreationDto.getImage().isEmpty() ? postCreationDto.getImage().getBytes() : null;
        } catch (IOException e) {
            throw new FileFormatException("Error when trying to convert MultipartFile to byte array");
        }
        post.setImage(imageBytes);

        if (postDao.update(post) == 1) {
            logger.info(String.format("Post %d was updated", post.getId()));
        } else {
            logger.info(String.format("Post %d NOT updated", post.getId()));
        }
    }

    @Override
    public void deleteById(Long id) {

        commentDao.findByPostId(id).forEach(c -> commentDao.deleteById(c.getId()));
        likeDao.clearRowByPostId(id);

        if (postDao.deleteById(id) == 1) {
            logger.info("Post was deleted by id " + id);
        } else {
            logger.warn("Post was NOT deleted by id " + id);
        }
    }

    @Override
    public int getNumberOfEntity() {
        return  postDao.getNumberOfEntity();
    }

    @Override
    public int getNumberOfEntityByUserId(Long id) {
        return  postDao.getNumberOfEntityByUserId(id);
    }

    @Override
    public List<Post> getPageWithLimitAndOffsetOrderByDataDESC(int pageSize, int offset) {
        return postDao.findAllWithLimitAndOffsetOrderByDataDESC(pageSize, offset);
    }

    @Override
    public List<Post> getPageByUserIdWithLimitAndOffsetOrderByDataDESC(Long id, int pageSize, int offset) {
        return postDao.findByUserIdWithLimitAndOffsetOrderByDataDESC(id, pageSize, offset);
    }

    @Override
    public List<Post> getPageWithLimitAndOffsetOrderByLikeDESC(int pageSize, int offset) {
        return postDao.findAllWithLimitAndOffsetOrderByLikeDESC(pageSize, offset);
    }

    @Override
    public List<Post> getPageByUserIdWithLimitAndOffsetOrderByLikeDESC(Long id, int pageSize, int offset) {
        return postDao.findByUserIdWithLimitAndOffsetOrderByLikeDESC(id, pageSize, offset);
    }

    public boolean isUserAlreadyLikePost(Long postId, Long userId) {
        return likeDao.isUserAlreadyLikePost(userId, postId);
    }

    @Override
    public int likePost(Long userId, Long postId) {

        if (!likeDao.isUserAlreadyLikePost(userId, postId)) {
            likeDao.addLike(userId, postId);
        }
        return likeDao.getPostLikeNumber(postId);
    }

    @Override
    public int unlikePost(Long userId, Long postId) {

        if (likeDao.isUserAlreadyLikePost(userId, postId)) {
            likeDao.cancelLike(userId, postId);
        }
        return likeDao.getPostLikeNumber(postId);
    }

    @Override
    public int getPostLikeNumber(Long postId) {
        return likeDao.getPostLikeNumber(postId);
    }
}

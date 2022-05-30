package com.coursework.persistence.services.imp;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.CommentDto;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.CommentDao;
import com.coursework.persistence.services.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImp implements CommentService {

    private final CommentDao commentDao;
    private final EntityMapper mapper;

    Logger logger = LoggerFactory.getLogger(PostServiceImp.class);

    @Autowired
    public CommentServiceImp(CommentDao commentDao, EntityMapper mapper) {
        this.commentDao = commentDao;
        this.mapper = mapper;
    }

    @Override
    public Comment getById(Long id) {
        return commentDao.findById(id).orElseThrow(() -> new NotFoundException("Comment not found by id - " + id));
    }

    @Override
    public int getNumberOfEntity() {
        return  commentDao.getNumberOfEntity();
    }

    @Override
    public List<Comment> getAllPostCommentsByPostId(Long postId) {
        return commentDao.findByPostId(postId);
    }

    @Override
    public int getNumberOfPostCommentByPostId(Long postId) {
        return commentDao.getNumberOfEntityByPostId(postId);
    }

    @Override
    public List<Comment> getAllUserComments(Long userId) {
        return commentDao.findByUserId(userId);
    }

    @Override
    public int getNumberOfUserCommentByUserId(Long userId) {
        return commentDao.getNumberOfEntityByUserId(userId);
    }

    @Override
    public Comment save(CommentDto commentDto, Long userId) {
        Comment comment = mapper.toEntity(commentDto);
        comment.setUserId(userId);
        comment.setCreationDate(LocalDateTime.now());

        if (commentDao.save(comment) == 1) {
            logger.info(String.format("User with id - %d add comment to post %d", comment.getUserId(),
                    comment.getPostId()));
        }

        return commentDao.findByCreationDate(Timestamp.valueOf(comment.getCreationDate()))
                .orElseThrow(() -> new NotFoundException("Comment not found by timestamp - "
                        + Timestamp.valueOf(comment.getCreationDate())));
    }

    @Override
    public void deleteById(Long id) {
        if (commentDao.deleteById(id) == 1) {
            logger.info("Comment was deleted by id " + id);
        }
    }
}

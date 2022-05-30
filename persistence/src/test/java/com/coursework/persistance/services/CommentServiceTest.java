package com.coursework.persistance.services;


import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.CommentDto;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.CommentDao;
import com.coursework.persistence.services.imp.CommentServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EntityMapper.class
        }
)
public class CommentServiceTest {

    @Mock
    private CommentDao commentDao;

    @Autowired
    private EntityMapper mapper;

    private CommentServiceImp commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImp(commentDao, mapper);
    }

    @Test
    @DisplayName("Get comment by id")
    void get_comment_by_id() {
        Comment testComment = new Comment();
        testComment.setId(1L);

        when(commentDao.findById(1L))
                .thenReturn(Optional.of(testComment));

        Comment comment = commentService.getById(1L);

        assertThat(comment).isEqualTo(testComment);
    }

    @Test
    @DisplayName("Get number of comments")
    void get_number_of_comments() {
        when(commentDao.getNumberOfEntity()).thenReturn(2);

        assertThat(commentService.getNumberOfEntity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Get all comments related to post")
    void get_all_post_comments() {
        Comment testComment = new Comment();
        testComment.setId(1);

        when(commentDao.findByPostId(anyLong())).thenReturn(
                List.of(
                        testComment
                )
        );

        assertThat(commentService.getAllPostCommentsByPostId(1L)).isEqualTo(
                List.of(testComment)
        );
    }

    @Test
    @DisplayName("Get number of post comment by post id")
    void get_number_of_post_comment_by_post_id() {
        commentService.getNumberOfPostCommentByPostId(1L);

        verify(commentDao).getNumberOfEntityByPostId(anyLong());
    }

    @Test
    @DisplayName("Get all user comments ")
    void get_all_user_comments() {
        Comment testComment = new Comment();
        testComment.setId(1);

        when(commentDao.findByUserId(anyLong())).thenReturn(
                List.of(
                        testComment
                )
        );

        assertThat(commentService.getAllUserComments(1L)).isEqualTo(
                List.of(testComment)
        );
    }

    @Test
    @DisplayName("Get number of user comment by user id")
    void get_number_of_user_comment_by_user_id() {
        commentService.getNumberOfUserCommentByUserId(1L);

        verify(commentDao).getNumberOfEntityByUserId(anyLong());
    }

    @Test
    @DisplayName("Save comment")
    void save_comment() {
        CommentDto testComment = new CommentDto();
        testComment.setId(1);

        when(commentDao.save(any(Comment.class))).thenReturn(1);
        when(commentDao.findByCreationDate(any(Timestamp.class))).thenReturn(Optional.of(new Comment()));

        commentService.save(testComment, 1L);

        verify(commentDao).findByCreationDate(any(Timestamp.class));

    }

    @Test
    @DisplayName("Failed save comment")
    void failed_save_comment() {
        CommentDto testComment = new CommentDto();
        testComment.setId(1);

        when(commentDao.save(any(Comment.class))).thenReturn(1);

        assertThatCode(() -> commentService.save(testComment, 1L))
                .isInstanceOf(NotFoundException.class);

    }

    @Test
    @DisplayName("Delete comment by id")
    void delete_comment_by_id() {
        commentService.deleteById(1L);

        verify(commentDao).deleteById(anyLong());
    }
}

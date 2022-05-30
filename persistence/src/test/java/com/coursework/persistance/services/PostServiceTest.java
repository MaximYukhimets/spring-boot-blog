package com.coursework.persistance.services;


import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.entity.Post;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.CommentDao;
import com.coursework.persistence.repository.LikeDao;
import com.coursework.persistence.repository.PostDao;
import com.coursework.persistence.services.imp.PostServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EntityMapper.class
        }
)

public class PostServiceTest {

    @Mock
    private PostDao postDao;

    @Mock
    private CommentDao commentDao;

    @Mock
    private LikeDao likeDao;

    @Autowired
    private EntityMapper mapper;

    private PostServiceImp postService;

    @BeforeEach
    void setUp() {
            postService = new PostServiceImp(postDao, commentDao, likeDao, mapper);
    }

    @Test
    @DisplayName("Post has been received by id")
    void post_has_been_received_by_id() {
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        when(postDao.findById(1L))
                .thenReturn(Optional.of(testPost));

        Post post = postService.getById(1L);

        assertThat(post).isEqualTo(testPost);
    }

    @Test
    @DisplayName("Post has not been received by id")
    void post_has_not_been_received_by_id() {
        when(postDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThatCode(() -> postService.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Return all user posts")
    void return_all_user_posts() {
        Post testPost1 = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.now(), 1);

        when(postDao.findByUserId(anyLong())).thenReturn(
                List.of(
                        testPost1,
                        testPost2
                )
        );

        List<Post> posts = postService.getAllByUserId(1L);

        assertThat(posts).isEqualTo(List.of(testPost1, testPost2));
    }

    @Test
    @DisplayName("Return all posts ordered by data")
    void return_all_posts_ordered_by_data() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Post testPost1 = new Post(1, "title", "body", LocalDateTime.parse("2019-03-04 11:30:40", formatter), 1);
        Post testPost2 = new Post(2, "title", "body", LocalDateTime.parse("2020-03-04 11:30:40", formatter), 1);

        when(postDao.findAll()).thenReturn(
                List.of(
                        testPost1,
                        testPost2
                )
        );

        List<Post> posts = postService.getAllOrderedByDate();

        assertThat(posts).isEqualTo(List.of(testPost2, testPost1));
    }

    @Test
    @DisplayName("Save post in storage")
    void save_post() {
        PostCreationDto postDto = new PostCreationDto();
        postDto.setImage(new MockMultipartFile("name", new byte[0]));

        postService.save(postDto, 1L);

        verify(postDao).save(any(Post.class));
    }

    @Test
    @DisplayName("Update post in storage")
    void update_post() {
        Post post = new Post();
        PostCreationDto postDto = new PostCreationDto();
        postDto.setImage(new MockMultipartFile("name", new byte[0]));

        postService.update(post, postDto);
        verify(postDao).update(any(Post.class));

    }

    @Test
    @DisplayName("Delete post")
    void delete_post_by_id() {
        postService.deleteById(1L);

        verify(postDao).deleteById(anyLong());
    }

    @Test
    @DisplayName("Return number of posts")
    void return_number_of_posts() {
        when(postDao.getNumberOfEntity()).thenReturn(1);

        assertThat(postService.getNumberOfEntity()).isEqualTo(1);

        verify(postDao).getNumberOfEntity();
    }

    @Test
    @DisplayName("Return number user posts")
    void return_number_of_user_posts() {
        when(postDao.getNumberOfEntityByUserId(1L)).thenReturn(1);

        assertThat(postService.getNumberOfEntityByUserId(1L)).isEqualTo(1);

        verify(postDao).getNumberOfEntityByUserId(anyLong());
    }

    @Test
    @DisplayName("Take part of posts sorted by date with limit and offset")
    void take_part_posts_values_ordered_by_date_with_limit_and_offset() {
        postService.getPageWithLimitAndOffsetOrderByDataDESC(1, 1);

        verify(postDao).findAllWithLimitAndOffsetOrderByDataDESC(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Take part of user posts sorted by date with limit and offset")
    void take_part_of_user_posts_values_ordered_by_date_with_limit_and_offset() {
        postService.getPageByUserIdWithLimitAndOffsetOrderByDataDESC(1L, 1, 2);

        verify(postDao).findByUserIdWithLimitAndOffsetOrderByDataDESC(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Take part of posts sorted by likes with limit and offset")
    void take_part_posts_values_ordered_by_likes_with_limit_and_offset() {
        postService.getPageWithLimitAndOffsetOrderByLikeDESC(1, 1);

        verify(postDao).findAllWithLimitAndOffsetOrderByLikeDESC(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Take part of user posts sorted by likes with limit and offset")
    void take_part_of_user_posts_values_ordered_by_likes_with_limit_and_offset() {
        postService.getPageByUserIdWithLimitAndOffsetOrderByLikeDESC(1L,1, 1);

        verify(postDao).findByUserIdWithLimitAndOffsetOrderByLikeDESC(anyLong(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("Check is user already like post")
    void is_user_already_like_post() {
        postService.isUserAlreadyLikePost(1L, 1L);

        verify(likeDao).isUserAlreadyLikePost(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Like if it hasn't been liked yet")
    void like_if_it_has_not_been_liked_yet() {
        when(likeDao.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(false);

        postService.likePost(1L, 1L);

        verify(likeDao).addLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Ignore if already liked")
    void ignore_if_already_liked() {
        when(likeDao.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(true);

        postService.likePost(1L, 1L);

        verify(likeDao, never()).addLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Cancel like if it has been set before")
    void cancel_like_if_it_has_been_set_before() {
        when(likeDao.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(true);

        postService.unlikePost(1L, 1L);

        verify(likeDao).cancelLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Ignore cancel like if it has not been set before")
    void ignore_cancel_like_if_it_has_not_been_set_before() {
        when(likeDao.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(false);

        postService.unlikePost(1L, 1L);

        verify(likeDao, never()).cancelLike(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Get post like number")
    void get_post_like_number() {
        when(likeDao.getPostLikeNumber(anyLong())).thenReturn(1);

        assertThat(postService.getPostLikeNumber(1L)).isEqualTo(1);
    }

}

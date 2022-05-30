package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.configuration.DatasourceConfig;
import com.coursework.persistence.repository.imp.RoleDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.PostSortingOrderConfig;
import com.coursework.web.config.PrincipalDetailsArgumentResolver;
import com.coursework.web.security.AuthenticationUserService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ContextConfiguration(
        classes = {
                AuthenticationUserService.class,
                UserDaoImp.class,
                RoleDaoImp.class,
                SecurityConfig.class,
                DatasourceConfig.class
        }
)

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(IndexController.class)
class IndexControllerTest {

    @Autowired
    @MockBean
    private PostService postService;

    @Autowired
    @MockBean
    private UserService userService;

    @Autowired
    @MockBean
    private CommentService commentService;

    @Autowired
    @MockBean
    private PostSortingOrderConfig postSortingOrderConfig;

    @Autowired
    private EntityMapper mapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new IndexController(postService, userService, commentService, mapper, postSortingOrderConfig))
                .setCustomArgumentResolvers(argumentResolver)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
    }

    @Test
    void setup_mockMvc() {
        assertNotNull(setUpMockMvc(new PrincipalDetailsArgumentResolver()));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void show_root_page_where_post_ordered_by_data() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        when(postService.getNumberOfEntity()).thenReturn(1);
        when(postService.getPostLikeNumber(anyLong())).thenReturn(1);
        when(userService.findById(anyLong())).thenReturn(new User());
        when(postService.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(true);
        when(commentService.getNumberOfPostCommentByPostId(anyLong())).thenReturn(1);

        when(postSortingOrderConfig.isSortingByLikeOn()).thenReturn(false);
        when(postService.getPageWithLimitAndOffsetOrderByDataDESC(anyInt(), anyInt()))
                .thenReturn(
                        List.of(testPost)
                );

        mockMvc
            .perform(MockMvcRequestBuilders.get("/").requestAttr("page", 1))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attributeExists("posts", "reducer", "pager"))
            .andExpect(MockMvcResultMatchers.view().name("/index"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void show_root_page_where_post_ordered_by_like() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);

        when(postService.getNumberOfEntity()).thenReturn(1);
        when(postService.getPostLikeNumber(anyLong())).thenReturn(1);
        when(userService.findById(anyLong())).thenReturn(new User());
        when(postService.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(true);
        when(commentService.getNumberOfPostCommentByPostId(anyLong())).thenReturn(1);

        when(postSortingOrderConfig.isSortingByLikeOn()).thenReturn(true);
        when(postService.getPageWithLimitAndOffsetOrderByDataDESC(anyInt(), anyInt()))
                .thenReturn(
                        List.of(testPost)
                );

        mockMvc
                .perform(MockMvcRequestBuilders.get("/").requestAttr("page", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("posts", "reducer", "pager"))
                .andExpect(MockMvcResultMatchers.view().name("/index"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void sorting_order_should_call_isSortingByLikeOn_method() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postSortingOrderConfig.isSortingByLikeOn()).thenReturn(true);

        MvcResult result  = mockMvc
                            .perform(MockMvcRequestBuilders.post("/sort-order").with(csrf()))
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("true");
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void sorting_order_should_call_setSortingByLikeOn_method() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/change-sort-order").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(postSortingOrderConfig).setSortingByLikeOn(anyBoolean());
    }

}


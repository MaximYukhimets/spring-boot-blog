package com.coursework.web.controllers;


import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.User;
import com.coursework.persistence.configuration.DatasourceConfig;
import com.coursework.persistence.repository.imp.RoleDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.config.NullPrincipalDetailsArgumentResolver;
import com.coursework.web.config.PrincipalDetailsArgumentResolver;
import com.coursework.web.security.AuthenticationUserService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
@WebMvcTest(PostController.class)
public class PostControllerTest {

    @MockBean
    PostService postService;

    @MockBean
    UserService userService;

    @MockBean
    CommentService commentService;

    @Autowired
    EntityMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new PostController(postService, userService, commentService, mapper))
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
    void new_post_should_return_post_creation_form_view() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.get("/new-post"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.model().attribute("post", new PostCreationDto()))
            .andExpect(MockMvcResultMatchers.view().name("/postForm"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void new_post_should_redirect_on_index_page_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.get("/new-post"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void create_post_endpoint_should_redirect_on_index_page_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.post("/new-post").with(csrf())
                    .flashAttr("post", new PostCreationDto("title", "body")))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void create_post_endpoint_should_return_post_form_page_when_binding_result_has_errors() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.post("/new-post").with(csrf())
                    .flashAttr("post", new PostCreationDto()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("/postForm"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void create_post_endpoint_should_call_save_method_and_redirect_on_index() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.post("/new-post").with(csrf())
                    .flashAttr("post", new PostCreationDto("title", "body")))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        verify(postService).save(any(PostCreationDto.class), anyLong());

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void show_post_should_return_post_view() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(1, "title", "body", LocalDateTime.now(), 1));
        when(userService.findById(anyLong())).thenReturn(new User(1, "user", "mail"));
        when(postService.getNumberOfEntity()).thenReturn(10);
        when(commentService.getNumberOfPostCommentByPostId(anyLong())).thenReturn(1);

        mockMvc
            .perform(MockMvcRequestBuilders.get("/post/{id}", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("post", "isBodyEmpty",
                        "isAuthorUser", "isUserAdminOrSuperAdmin"))
                .andExpect(MockMvcResultMatchers.view().name("/post"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_post_should_redirect_on_index_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(1, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/post/{id}/edit", 1))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_post_should_call_update_method_and_redirect_on_post_when_user_auth_and_no_error() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(2, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/edit", 1).with(csrf())
                        .flashAttr("post", new PostCreationDto("title", "body")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/post/1"));

        verify(postService).update(any(Post.class), any(PostCreationDto.class));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_post_should_redirect_on_post_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(2, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/edit", 1).with(csrf())
                        .flashAttr("post", new PostCreationDto("title", "body")))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/post/1"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_post_should_return_edit_post_view_when_binding_results_has_errors() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(2, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/edit", 1).with(csrf())
                        .flashAttr("post", new PostCreationDto()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/editPost"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_post_should_delete_post_when_user_auth_and_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(2, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/delete", 1).with(csrf())
                        .requestAttr("redirectOn", ""))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

        verify(postService).deleteById(anyLong());

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_post_should_redirect_on_user_profile_when_username_not_empty() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getById(anyLong())).thenReturn(new Post(2, "title", "body", LocalDateTime.now(), 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/delete", 1).with(csrf())
                        .param("redirectOn", "username"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/username"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void like_post_should_call_like_post_method_when_user_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.likePost(anyLong(), anyLong())).thenReturn(1);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/like", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("1");

        verify(postService).likePost(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void like_post_should_not_call_like_post_method_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/like", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("0");

        verify(postService, never()).likePost(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void unlike_post_should_call_unlike_post_method_when_user_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.unlikePost(anyLong(), anyLong())).thenReturn(1);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/unlike", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("1");

        verify(postService).unlikePost(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void unlike_post_should_not_call_unlike_post_method_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.post("/post/{id}/unlike", 1).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("0");

        verify(postService, never()).unlikePost(anyLong(), anyLong());
    }
}

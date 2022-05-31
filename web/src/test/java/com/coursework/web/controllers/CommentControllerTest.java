package com.coursework.web.controllers;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.CommentDto;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.User;
import com.coursework.persistence.configuration.DatasourceConfig;
import com.coursework.persistence.repository.imp.RoleDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.config.NullPrincipalDetailsArgumentResolver;
import com.coursework.web.config.PrincipalDetailsArgumentResolver;
import com.coursework.web.config.PrincipalDetailsArgumentResolverWithCustomRoles;
import com.coursework.web.security.AuthenticationUserService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
@WebMvcTest(CommentController.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CommentControllerTest {

    @Autowired
    @MockBean
    private UserService userService;

    @Autowired
    @MockBean
    private CommentService commentService;

    @Autowired
    private EntityMapper mapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new CommentController(userService, commentService, mapper))
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
    void get_post_comment_return_post_comments() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        User testUser = new User(1, "username", "mail");
        testUser.setImage(new byte[0]);

        when(commentService.getAllPostCommentsByPostId(anyLong())).thenReturn(
                List.of(new Comment(1, "body", 1, LocalDateTime.now(), 1))
        );
        when(userService.findById(anyLong())).thenReturn(testUser);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/comments")
                        .param("postId", String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void add_comment_call_save_method_when_user_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        User testUser = new User(1, "username", "mail");
        Comment testComment = new Comment(1, "body", 1, LocalDateTime.now(), 1);
        testUser.setImage(new byte[0]);

        when(userService.findById(anyLong())).thenReturn(testUser);
        when(commentService.save(any(CommentDto.class), anyLong())).thenReturn(testComment);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/add-comment").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\": \"1\", \"body\":\"body\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists());

        verify(commentService).save(any(CommentDto.class), anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_user_should_call_delete_by_id_method_when_user_auth_and_author() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        Comment testComment = new Comment(1, "body", 1, LocalDateTime.now(), 1);

        when(commentService.getById(anyLong())).thenReturn(testComment);

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/delete-comment/{id}", 1)
                                .with(csrf()))
                .andExpect(status().isOk());

        verify(commentService).deleteById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_user_should_call_delete_by_id_method_when_user_auth_and_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));
        Comment testComment = new Comment(1, "body", 1, LocalDateTime.now(), 1);

        when(commentService.getById(anyLong())).thenReturn(testComment);

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/delete-comment/{id}", 2)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(commentService).deleteById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_user_should_not_call_delete_by_id_method_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());
        Comment testComment = new Comment(1, "body", 1, LocalDateTime.now(), 1);

        when(commentService.getById(anyLong())).thenReturn(testComment);

        mockMvc
                .perform(MockMvcRequestBuilders.delete("/delete-comment/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(commentService, never()).deleteById(anyLong());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_current_user_return_current_user_if_user_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/current-user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists());

    }
}

package com.coursework.web.controllers;


import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.entity.Comment;
import com.coursework.domain.entity.Post;
import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.persistence.configuration.DatasourceConfig;
import com.coursework.persistence.repository.imp.RoleDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import com.coursework.persistence.services.CommentService;
import com.coursework.persistence.services.PostService;
import com.coursework.persistence.services.RoleService;
import com.coursework.persistence.services.UserService;
import com.coursework.web.PostSortingOrderConfig;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(
        classes = {
                BCryptPasswordEncoder.class,
                AuthenticationUserService.class,
                UserDaoImp.class,
                RoleDaoImp.class,
                SecurityConfig.class,
                DatasourceConfig.class
        }
)

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(RegistrationController.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AdminPanelControllerTest {

    @Autowired
    @MockBean
    private UserService userService;

    @Autowired
    @MockBean
    private PostService postService;

    @Autowired
    @MockBean
    private RoleService roleService;

    @Autowired
    @MockBean
    private CommentService commentService;


    @Autowired
    private EntityMapper mapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new AdminPanelController(userService, postService, roleService, commentService, mapper))
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
    void get_all_users_should_return_all_user() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        User testUser = new User(1, "username", "mail");
        testUser.setImage(new byte[0]);
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setImage(new byte[0]);
        Role testRole = new Role(1, "TEST_ROLE");

        when(userService.findAll()).thenReturn(
                List.of(testUser)
        );
        when(roleService.getAllUserRoleByUserId(anyLong())).thenReturn(
                Set.of(testRole)
        );

        mockMvc
                .perform(MockMvcRequestBuilders.get("/get-all-users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").exists()).andReturn();
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_all_users_should_return_empty_list_if_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/get-all-users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].id").doesNotExist());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_admin_panel_should_redirect_on_index_page_if_user_not_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.get("/admin-panel"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_admin_panel_should_return_admin_panel_view_if_user_admin_or_super_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/admin-panel"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/admin"));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_content_should_return_content_if_user_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        when(postService.getNumberOfEntity()).thenReturn(1);
        when(commentService.getNumberOfEntity()).thenReturn(1);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/get-content"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json")).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("{\"postCount\":1,\"commentCount\":1}");
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void get_content_should_return_empty_body_if_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders.get("/get-content"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json")).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("{}");
    }
}

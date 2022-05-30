package com.coursework.web.controllers;


import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.EditUserDto;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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
@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

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
    private RoleService roleService;

    @Autowired
    @MockBean
    private PostSortingOrderConfig postSortingOrderConfig;

    @Autowired
    private EntityMapper mapper;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;


    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new ProfileController(userService, postService, commentService, roleService, mapper, postSortingOrderConfig))
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
    void show_profile_page_where_post_ordered_by_data() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        User testUser = new User(1, "username", "mail");
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setImage(new byte[0]);
        Post testPost = new Post(1, "title", "body", LocalDateTime.now(), 1);
        Role testRole = new Role(1, "TEST_ROLE");

        when(userService.findByUsername(anyString())).thenReturn(testUser);
        when(roleService.getAllUserRoleByUserId(anyLong())).thenReturn(
                Set.of(testRole)
        );

        when(postService.getNumberOfEntityByUserId(anyLong())).thenReturn(1);
        when(postSortingOrderConfig.isSortingByLikeOn()).thenReturn(false);

        when(postService.getPageByUserIdWithLimitAndOffsetOrderByDataDESC(anyLong(), anyInt(), anyInt()))
                .thenReturn(
                        List.of(testPost)
                );
        when(postService.getPostLikeNumber(anyLong())).thenReturn(1);
        when(commentService.getNumberOfPostCommentByPostId(anyLong())).thenReturn(1);
        when(postService.isUserAlreadyLikePost(anyLong(), anyLong())).thenReturn(true);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/{username}", "user")
                        .requestAttr("page", 1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user", "isAdminOrSuperAdmin",
                        "isAdmin", "posts", "reducer", "pager"))
                .andExpect(MockMvcResultMatchers.view().name("/profile"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void upload_image_should_call_updateImageByUser_if_user_auth_and_profile_owner() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/user/{username}/upload", "user")
                                .file("file", file.getBytes()).with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService).updateImageByUsername(any(MockMultipartFile.class), anyString());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void upload_image_should_not_call_updateImageByUser_if_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/user/{username}/upload", "user")
                        .file("file", file.getBytes()).with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService, never()).updateImageByUsername(any(MockMultipartFile.class), anyString());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void upload_image_should_not_call_updateImageByUser_if_user_not_profile_owner() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/user/{username}/upload", "another_user")
                        .file("file", file.getBytes()).with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/another_user"));

        verify(userService, never()).updateImageByUsername(any(MockMultipartFile.class), anyString());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_profile_should_return_view_when_user_auth_and_owner() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        User testUser = new User(1, "username", "mail");
        testUser.setRegistrationDate(LocalDateTime.now());
        testUser.setImage(new byte[0]);

        when(userService.findByUsername(anyString())).thenReturn(testUser);

        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/{username}/edit", "user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.view().name("/editProfile"));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_profile_should_redirect_on_profile_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/{username}/edit", "user"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_profile_should_call_update_method() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        EditUserDto testUser = new EditUserDto(1, "first", "last", "mail@com", "about");

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/edit", "user").with(csrf())
                        .flashAttr("user", testUser))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService).update(any(EditUserDto.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_profile_should_redirect_on_profile_and_not_call_update_method_if_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());
        EditUserDto testUser = new EditUserDto(1, "first", "last", "mail@com", "about");

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/edit", "user").with(csrf())
                        .flashAttr("user", testUser))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService, never()).update(any(EditUserDto.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void edit_profile_should_return_edit_profile_view_when_binding_result_has_errors() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        EditUserDto testUser = new EditUserDto(1, "", "last", "mail@com", "about");

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/edit", "user").with(csrf())
                        .flashAttr("user", testUser))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/editProfile"));

        verify(userService, never()).update(any(EditUserDto.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void ban_user_should_redirect_on_profile_if_user_who_bans_not_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/ban", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService, never()).update(any(User.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void ban_user_should_call_update_method_when_user_admin_or_super_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));
        User testUser = new User(1, "username", "mail");
        testUser.setActive(true);

        when(userService.findByUsername(anyString())).thenReturn(testUser);
        when(roleService.getAllUserRoleByUserId(anyLong())).thenReturn(Set.of(new Role(1, "USER")));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/ban", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService).update(any(User.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void unban_user_should_redirect_on_profile_if_user_who_bans_not_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/unban", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService, never()).update(any(User.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void unban_user_should_call_update_method_when_user_admin_or_super_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));
        User testUser = new User(1, "username", "mail");
        testUser.setActive(true);

        when(userService.findByUsername(anyString())).thenReturn(testUser);

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/unban", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(userService).update(any(User.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void make_admin_should_redirect_on_profile_if_user_not_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/make-admin", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(roleService, never()).addUserRole(any(User.class), any(Role.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void make_admin_should_call_update_method_if_user_admin_or_super_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");
        roles.add("SUPER_ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));
        User testUser = new User(1, "username", "mail");
        testUser.setActive(true);

        when(userService.findByUsername(anyString())).thenReturn(testUser);
        when(roleService.getAllUserRoleByUserId(anyLong())).thenReturn(Set.of(new Role(1, "USER")));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/make-admin", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(roleService).addUserRole(any(User.class), any(Role.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_admin_should_redirect_on_profile_if_user_not_admin_or_super_admin() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/delete-admin", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(roleService, never()).addUserRole(any(User.class), any(Role.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void delete_admin_should_call_update_method_if_user_admin_or_super_admin() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        roles.add("ADMIN");
        roles.add("SUPER_ADMIN");

        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolverWithCustomRoles(roles));
        User testUser = new User(1, "username", "mail");
        testUser.setActive(true);

        when(userService.findByUsername(anyString())).thenReturn(testUser);
        when(roleService.getAllUserRoleByUserId(anyLong())).thenReturn(Set.of(new Role(1, "USER"),
                new Role(1, "ADMIN")));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/{username}/delete-admin", "user").with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user/user"));

        verify(roleService).deleteUserRole(any(User.class), any(Role.class));
    }

}

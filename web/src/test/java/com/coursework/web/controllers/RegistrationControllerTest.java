package com.coursework.web.controllers;


import com.coursework.domain.dto.PostCreationDto;
import com.coursework.domain.dto.UserDto;
import com.coursework.persistence.configuration.DatasourceConfig;
import com.coursework.persistence.repository.imp.RoleDaoImp;
import com.coursework.persistence.repository.imp.UserDaoImp;
import com.coursework.persistence.services.UserService;
import com.coursework.web.config.NullPrincipalDetailsArgumentResolver;
import com.coursework.web.config.PrincipalDetailsArgumentResolver;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

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
public class RegistrationControllerTest {

    @Autowired
    @MockBean
    private UserService userService;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc setUpMockMvc(HandlerMethodArgumentResolver argumentResolver) {
        return MockMvcBuilders
                .standaloneSetup(new RegistrationController(userService))
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
    void registration_should_redirect_to_index_page_if_user_is_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.get("/registration"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/"));

    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void registration_should_should_return_registration_view_when_user_not_auth() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new NullPrincipalDetailsArgumentResolver());

        mockMvc
            .perform(MockMvcRequestBuilders.get("/registration"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.view().name("/registrationForm"))
            .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void registration_new_user_should_return_registration_view_when_binding_result_has_errors() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        UserDto testUser = new UserDto();

        mockMvc
                .perform(MockMvcRequestBuilders.post("/registration").with(csrf())
                        .flashAttr("user", testUser))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/registrationForm"));

        verify(userService, never()).save(any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void registration_new_user_should_save_user_when_binding_result_has_no_error() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());
        UserDto testUser = new UserDto();
        testUser.setUsername("user");
        testUser.setFirstName("first");
        testUser.setLastName("last");
        testUser.setPassword("pass");
        testUser.setRepeatPassword("pass");
        testUser.setEmail("mail@mail");

        mockMvc
                .perform(MockMvcRequestBuilders.post("/registration").with(csrf())
                        .flashAttr("user", testUser))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("/success"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));

        verify(userService).save(any(UserDto.class));
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void check_strength_should_return_empty_string_if_password_empty() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        MvcResult result =mockMvc
                .perform(MockMvcRequestBuilders.get("/check-strength")
                        .param("password", String.valueOf("")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("");
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void check_strength_should_return_weak_if_password_in_weak_password_range() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        MvcResult result =mockMvc
                .perform(MockMvcRequestBuilders.get("/check-strength")
                        .param("password", String.valueOf("123")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("weak");
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void check_strength_should_return_medium_if_password_in_medium_password_range() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        MvcResult result =mockMvc
                .perform(MockMvcRequestBuilders.get("/check-strength")
                        .param("password", String.valueOf("123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("medium");
    }

    @Test
    @WithMockUser(username = "user", password = "pass", roles = "USER")
    void check_strength_should_return_strong_if_password_in_strong_password_range() throws Exception {
        MockMvc mockMvc = setUpMockMvc(new PrincipalDetailsArgumentResolver());

        MvcResult result =mockMvc
                .perform(MockMvcRequestBuilders.get("/check-strength")
                        .param("password", String.valueOf("123456789")))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("strong");
    }

}

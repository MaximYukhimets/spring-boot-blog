package com.coursework.persistance.services;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.dto.UserDto;
import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.domain.exceptions.NotFoundException;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.services.imp.UserServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(
        classes = {
                EntityMapper.class,
                BCryptPasswordEncoder.class
        }
)

public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Autowired
    private EntityMapper mapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private BindingResult bindingResult;

    private UserServiceImp userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImp(userDao, roleDao, mapper, bCryptPasswordEncoder);
    }


    @Test
    @DisplayName("Empty list of users is returned in case when no users in storage")
    void empty_list_of_users_returned_in_case_when_nothing_in_storage() {
        when(userDao.findAll()).thenReturn(emptyList());

        List<User> users = userService.findAll();

        assertThat(users).isEmpty();
    }

    @Test
    @DisplayName("User was found by id")
    void user_was_found_by_id() {
        when(userDao.findById(1L))
                .thenReturn(Optional.of(new User (1, "username", "@mail")));

        User user = userService.findById(1L);

        assertThat(user).isEqualTo(new User (1, "username", "@mail"));
    }

    @Test
    @DisplayName("Throw exception when user not found by id in storage")
    void throw_exception_when_user_not_found_by_id_in_storage() {
        when(userDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThatCode(() -> userService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Throw exception when user not found by username in storage")
    void throw_exception_when_user_not_found_by_username_in_storage() {
        when(userDao.findByUserName("username"))
                .thenReturn(Optional.empty());

        assertThatCode(() -> userService.findByUsername("username"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Throw exception when user not found by username in storage")
    void throw_exception_when_user_not_found_by_email_in_storage() {
        when(userDao.findByEmail("mail"))
                .thenReturn(Optional.empty());

        assertThatCode(() -> userService.findByEmail("mail"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("User validation will fail when user with this email already exist")
    void user_validation_will_fail_when_user_with_this_email_already_exist() {
        UserDto userDto = new UserDto();
        userDto.setEmail("mail");
        userDto.setUsername("username");
        userDto.setPassword("pass");

        when(userDao.findByEmail(userDto.getEmail()))
                .thenReturn(Optional.of(new User(1, "username", "mail")));

        userService.userValidation(userDto, bindingResult);

        verify(bindingResult).rejectValue("email", "error.user",
                "* User with this email address already exists");
    }

    @Test
    @DisplayName("User validation will fail when user with this username already exist")
    void user_validation_will_fail_when_user_with_this_username_already_exist() {
        UserDto userDto = new UserDto();
        userDto.setEmail("mail");
        userDto.setUsername("username");
        userDto.setPassword("pass");

        when(userDao.findByUserName(userDto.getUsername()))
                .thenReturn(Optional.of(new User(1, "username", "mail")));

        userService.userValidation(userDto, bindingResult);

        verify(bindingResult).rejectValue("username", "error.user",
                "* User with this username already exists");
    }

    @Test
    @DisplayName("User validation will fail when user password and verifycation password not the same")
    void user_validation_will_fail_when_password_and_repeat_password_different() {
        UserDto userDto = new UserDto();
        userDto.setEmail("mail");
        userDto.setUsername("username");
        userDto.setPassword("pass");
        userDto.setRepeatPassword("another_pass");

        userService.userValidation(userDto, bindingResult);

        verify(bindingResult).rejectValue("repeatPassword", "error.user",
                "* Passwords must match");
    }

    @Test
    @DisplayName("Edit user validation will fail when user try to set already taken email")
    void edit_user_validation_will_fail_when_user_try_to_set_already_taken_email() {
        EditUserDto editUserDto = new EditUserDto();
        editUserDto.setUsername("username");
        editUserDto.setEmail("another mail");

        when(userDao.findByUserName("username"))
                .thenReturn(Optional.of(new User(1, "username", "mail")));

        when(userDao.findByEmail(any()))
                .thenReturn(Optional.of(new User(1, "username", "mail")));

        userService.editUserDtoValidation(editUserDto, bindingResult);

        verify(bindingResult).rejectValue("email", "error.user",
                "* This email is already taken");
    }

    @Test
    @DisplayName("Save user in storage")
    void save_user() {
        UserDto userDto = new UserDto();
        userDto.setUsername("username");
        userDto.setPassword("pass");

        when(roleDao.getRoleByRoleName("USER"))
                .thenReturn(Optional.of(new Role(1, "USER")));

        when(userDao.findByUserName("username"))
                .thenReturn(Optional.of(new User(1, "username", "mail")));

        userService.save(userDto);

        verify(userDao).save(any(User.class));
        verify(roleDao).saveUserRole(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Update user all values")
    void update_user_all_values() {
        User user = new User();

        userService.update(user);

        verify(userDao).update(any(User.class));
    }

    @Test
    @DisplayName("Update user part values")
    void update_user_part_values() {
        EditUserDto editUserDto = new EditUserDto();

        userService.update(editUserDto);

        verify(userDao).update(any(EditUserDto.class));
    }

}

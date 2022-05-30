package com.coursework.persistence.services.imp;

import com.coursework.domain.EntityMapper;
import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.entity.Role;
import com.coursework.domain.entity.User;
import com.coursework.domain.dto.UserDto;
import com.coursework.domain.exceptions.FileFormatException;
import com.coursework.persistence.repository.RoleDao;
import com.coursework.persistence.repository.UserDao;
import com.coursework.persistence.services.UserService;
import com.coursework.domain.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final EntityMapper mapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String DEFAULT_ROLE = "USER";

    Logger logger = LoggerFactory.getLogger(UserServiceImp.class);

    @Autowired
    public UserServiceImp(UserDao userDao, RoleDao roleDao, EntityMapper mapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.mapper = mapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findById(Long id) {
        return userDao.findById(id).orElseThrow(
                () -> new NotFoundException("User not found by id - " + id));
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUserName(username).orElseThrow(
                () -> new NotFoundException("User not found by username - " + username));
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email).orElseThrow(
                () -> new NotFoundException("User not found by email - " + email));
    }

    @Override
    public void userValidation(UserDto userDto, BindingResult bindingResult) {
        if (userDao.findByEmail(userDto.getEmail()).isPresent()) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "* User with this email address already exists");
        }
        if (userDao.findByUserName(userDto.getUsername()).isPresent()) {
            bindingResult
                    .rejectValue("username", "error.user",
                            "* User with this username already exists");
        }

        if (!userDto.getPassword().equals(userDto.getRepeatPassword())) {
            bindingResult
                    .rejectValue("repeatPassword", "error.user",
                            "* Passwords must match");
        }
    }

    @Override
    public void editUserDtoValidation(EditUserDto editUserDto, BindingResult bindingResult) {
        User user = findByUsername(editUserDto.getUsername());

        if (!user.getEmail().equals(editUserDto.getEmail()) && userDao.findByEmail(editUserDto.getEmail()).isPresent()) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "* This email is already taken");
        }
    }

    @Override
    public void save(UserDto userDto) {
        User user = mapper.toEntity(userDto);

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setActive(true);
        try {
            user.setImage(this.getClass().getClassLoader().getResourceAsStream("img/default.jpg").readAllBytes());
        } catch (IOException e) {
            throw new FileFormatException(e.getMessage());
        }

        userDao.save(user);

        Role role = roleDao.getRoleByRoleName(DEFAULT_ROLE).orElseThrow(
                () -> new NotFoundException(String.format("Role %S not found by email - ", DEFAULT_ROLE)));

        User newUser = userDao.findByUserName(user.getUsername()).orElseThrow(
                () -> new NotFoundException("User not found by username - " + user.getUsername()));

        roleDao.saveUserRole(newUser.getId(), role.getId());

        logger.info("New user has been registered");
    }

    @Override
    public void update(User user) {
        if (userDao.update(user) == 1) {
            logger.info(String.format("User %s was updated", user.getUsername()));
        } else {
            logger.warn(String.format("User %s was NOT updated", user.getUsername()));
        }
    }

    @Override
    public void update(EditUserDto userDto) {
        if (userDao.update(userDto) == 1) {
            logger.info(String.format("User %s was updated", userDto.getUsername()));
        } else {
            logger.warn(String.format("User %s was NOT updated", userDto.getUsername()));
        }

    }

    @Override
    public void updateImageByUsername(MultipartFile file, String username) {
        if (file.isEmpty()) {
            throw new FileFormatException("File is empty!");
        }

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (IOException e) {
            throw new FileFormatException("Error when trying to convert MultipartFile to byte array");
        }

        if (userDao.saveImageForUserByUsername(imageBytes, username) == 1) {
            logger.info(String.format("Image has been updated for user : %s", username));
        } else {
            logger.warn(String.format("Image has NOT been updated for user : %s", username));
        }
    }

}

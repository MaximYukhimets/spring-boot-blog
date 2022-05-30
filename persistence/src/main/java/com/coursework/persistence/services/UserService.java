package com.coursework.persistence.services;

import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.entity.User;
import com.coursework.domain.dto.UserDto;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<User> findAll();

    User findById(Long id);

    User findByUsername(String username);

    User findByEmail(String email);

    void save(UserDto user);

    void userValidation(UserDto userDto, BindingResult bindingResult);

    void update(User user);

    void update(EditUserDto userDto);

    void updateImageByUsername(MultipartFile file, String username);

    void editUserDtoValidation(EditUserDto editUserDto, BindingResult bindingResult);
}



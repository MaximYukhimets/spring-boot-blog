package com.coursework.persistence.repository;

import com.coursework.domain.dto.EditUserDto;
import com.coursework.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String mail);

    int save(User user);

    int update(User user);

    int update(EditUserDto user);

    Integer getNumberOfEntity();

    int deleteById(Long id);

    int deleteByUsername(String username);

    int saveImageForUserByUsername(byte[] imageBytes, String username);
}

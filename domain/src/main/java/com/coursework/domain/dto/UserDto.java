package com.coursework.domain.dto;

import com.coursework.domain.entity.Role;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

    private long id;

    @NotEmpty(message = "* Username can't be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 40, message = "* Username should be between 4 and 40 characters")
    private String username;

    @NotEmpty(message = "* First name can't be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* First name should be between 4 and 60 characters")
    private String firstName;

    @NotEmpty(message = "* Last name can't be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* Last name should be between 4 and 60 characters")
    private String lastName;

    @NotEmpty(message = "* Password should not be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* Password should be between 4 and 60 characters")
    private String password;

    @NotEmpty(message = "* Password should not be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* Password should be between 4 and 60 characters")
    private String repeatPassword;

    @NotEmpty(message = "* Email should not be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Email(message = "* Email should be valid")
    private String email;

    private String about;

    private Date registrationDate;

    private String image64;

    private List<Role> roles;

    private boolean active;

}

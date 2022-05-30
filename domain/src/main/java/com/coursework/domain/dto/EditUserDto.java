package com.coursework.domain.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class EditUserDto {

    private long id;

    private String username;

    @NotEmpty(message = "* First name can't be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* First name should be between 4 and 60 characters")
    private String firstName;

    @NotEmpty(message = "* Last name can't be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Size(min = 4, max = 60, message = "* Last name should be between 4 and 60 characters")
    private String lastName;

    @NotEmpty(message = "* Email should not be empty.")
    @NotBlank(message = "* Username can't be blank.")
    @Email(message = "* Email should be valid")
    private String email;

    @Size(max = 350, message = "About Me should be less then 350 characters")
    private String about;

    public EditUserDto(long id, String firstName, String lastName, String email, String about) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.about = about;
    }
}

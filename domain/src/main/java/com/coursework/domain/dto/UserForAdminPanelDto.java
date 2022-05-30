package com.coursework.domain.dto;

import com.coursework.domain.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class UserForAdminPanelDto {

    private long id;

    private String username;

    private String email;

    private Date registrationDate;

    private boolean active;

    private List<Role> roles;

    private String image64;
}

package com.coursework.domain.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {

    private long id;
    private String username;
    private String firstName;
    private String lastSName;
    private String password;
    private String email;
    private String about;
    private LocalDateTime registrationDate;
    private Set<Role> roles = new HashSet<>();
    private Collection<Post> posts;
    private boolean active;
    private byte[] image;

    public User(long id, String username, String firstName, String lastSName, String password, String email, LocalDateTime registrationDate, boolean active) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastSName = lastSName;
        this.password = password;
        this.email = email;
        this.registrationDate = registrationDate;
        this.active = active;
    }

    public User(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }
}

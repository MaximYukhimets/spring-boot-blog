package com.coursework.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Post {

    private long id;
    private String title;
    private String body;
    private LocalDateTime creationDate;
    private Collection<Comment> comments;
    private User user;
    private long userId;
    private byte[] image;

    public Post(long id, String title, String body, LocalDateTime creationDate, long userId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.creationDate = creationDate;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

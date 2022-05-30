package com.coursework.domain.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Comment {

    private long id;

    private String body;

    private long userId;

    private LocalDateTime creationDate;

    private long postId;

    public Comment(long id, String body, long userId, LocalDateTime creationDate, long postId) {
        this.id = id;
        this.body = body;
        this.userId = userId;
        this.creationDate = creationDate;
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

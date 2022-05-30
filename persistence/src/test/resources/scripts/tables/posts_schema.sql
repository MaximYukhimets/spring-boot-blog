create table if not exists posts
(
    id              INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title           varchar not null,
    body            text,
    creation_date   timestamp    not null,
    user_id         bigint  not null references users (id),
    image           bytea
);
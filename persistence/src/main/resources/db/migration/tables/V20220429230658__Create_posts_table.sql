create table posts
(
    id              bigint primary key generated always as identity,
    title           varchar not null,
    body            text,
    creation_date   timestamp    not null,
    user_id         bigint  not null references users (id),
    image           bytea
);
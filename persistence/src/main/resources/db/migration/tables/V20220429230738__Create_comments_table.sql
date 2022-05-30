create table comments
(
    id            bigint primary key generated always as identity,
    body          text   not null,
    user_id       bigint not null references users (id),
    creation_date timestamp   not null,
    post_id       bigint not null references posts (id)
);
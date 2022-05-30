create table users
(
    id          bigint primary key generated always as identity,
    username    varchar(255) not null unique,
    first_name  varchar(255) not null,
    second_name varchar(255) not null,
    password    varchar(255) not null,
    email       varchar(255) not null unique,
    about       text,
    registration_date timestamp not null,
    active      boolean not null,
    image       bytea
);
create table roles (
    id            bigint primary key generated always as identity,
    role          varchar(255) not null unique
)
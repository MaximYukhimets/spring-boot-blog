create table if not exists users_posts (
     user_id bigint not null,
     post_id bigint not null,
     primary key (user_id, post_id),
     foreign key (user_id) references users(id),
     foreign key (post_id) references posts(id)
);
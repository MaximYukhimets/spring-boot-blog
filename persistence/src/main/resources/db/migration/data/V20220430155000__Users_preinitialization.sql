-- Set roles
insert into roles(role) values ('USER'), ('ADMIN'), ('SUPER_ADMIN');

-- Set users
-- password: pass
insert into users(username, first_name, second_name, password, email, registration_date, active) values
    ('super_admin',  'Hunter', 'Thompson', '$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi', 'mail@1234.com', '2022-04-30 23:04:55', 'yes');
-- password: pass
insert into users(username, first_name, second_name, password, email, registration_date, active) values
    ('admin',  'Oldos', 'Huxley', '$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi', 'mail@123.com', '2022-04-30 23:04:55', 'yes');
-- password: pass
insert into users(username, first_name, second_name, password, email, registration_date, active) values
    ('user',  'Chris', 'Chan', '$2a$10$UZ3meAZyru/14gzgZdjB5OCvJPSk4Bsc3ZHQgs34R1RY9lAAxXyYi', 'mail@12345.com', '2022-04-30 23:04:55', 'yes');

-- Assign roles to users
insert into users_roles(user_id, role_id) values (1, 1), (1, 2), (1, 3);
insert into users_roles(user_id, role_id) values (2, 1), (2, 2);
insert into users_roles(user_id, role_id) values (3, 1);

-- Posts
insert into posts(title, body, creation_date, user_id) values
('Title 1', 'You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.',
'2022-05-05 17:39:38.306403', 3);
insert into posts(title, body, creation_date, user_id) values
('Title 2', 'We get pre-generics behavior with raw types. Therefore, a raw type List accepts Object and can hold an element of any data type. This can lead to type safety issues when we mix parameterized and raw types.
Lets see this by creating some code that instantiates a List<String> before passing it to a method that accepts raw type List and adds an Integer to it:',
'2022-05-05 17:41:03.836393', 3);
insert into posts(title, body, creation_date, user_id) values
('Title 3', 'По известным причинам, бэкенд не может отдавать данные из репозитория как есть. Самая известная — сущностные зависимости берутся из базы не в таком виде, в котором их может понять фронт. Сюда же можно добавить и сложности с парсингом enum (если поля enum содержат дополнительные параметры), и многие другие сложности, возникающие при автоматическом приведении типов (или невозможности автоматического их приведения). Отсюда вытекает необходимость в использовании Data Transfer Object — DTO, понятном и для бэка, и для фронта.',
'2022-05-02 17:54:20.000000', 2);
insert into posts(title, body, creation_date, user_id) values
('Title 4', 'You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.',
'2022-05-05 17:40:13.577202', 3);
insert into posts(title, body, creation_date, user_id) values
('Title 5', 'You can run the application from the command line with Gradle or Maven. You can also build a single executable JAR file that contains all the necessary dependencies, classes, and resources and run that. Building an executable jar makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.',
'2022-05-05 17:39:23.114148', 3);
insert into posts(title, body, creation_date, user_id) values
('Title 6', 'Поэтому, правильным решением является использование библиотеки-маппера. Мне известны modelmapper и mapstruct. Поскольку я работал с modelmapper, я расскажу о нём, но если ты, мой читатель, хорошо знаком с mapstruct и можешь рассказать обо всех тонкостях её применения, напиши об этом, пожалуйста, статью, и я первый её заплюсую (мне эта библиотека также очень интересна, но въезжать в неё пока нет времени).',
'2022-05-02 17:35:57.157233', 1);
insert into posts(title, body, creation_date, user_id) values
('Title 7', 'Thymeleaf’s core is a DOM processing engine. Specifically, it uses its own high-performance DOM implementation —not the standard DOM API— for building in-memory tree representations of your templates, on which it later operates by traversing their nodes and executing processors on them that modify the DOM according to the current configuration and the set of data that is passed to the template for its representation —known as the context.
The use of a DOM template representation makes it very well suited for web applications because web documents are very often represented as object trees (in fact DOM trees are the way browsers represent web pages in memory). Also, building on the idea that most web applications use only a few dozen templates, that these are not big files and that they don’t normally change while the application is running, Thymeleaf’s usage of an in-memory cache of parsed template DOM trees allows it to be fast in production environments, because very little I/O is needed (if any) for most template processing operations.',
'2022-05-05 17:50:38.300000', 2);

-- Comment
insert into comments(body, user_id, creation_date, post_id) values
('first comment', 2, '2022-05-03 14:52:44.000000', 2);
insert into comments(body, user_id, creation_date, post_id) values
('second comment', 2, '2022-05-03 14:52:44.000000', 2);
insert into comments(body, user_id, creation_date, post_id) values
('therd comment', 2, '2022-05-03 14:52:44.000000', 3);
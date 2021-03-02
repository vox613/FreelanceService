-- Скрипт для заполнения таблиц с возможными статусами и ролями пользователей/заданий/договоров

DROP TABLE IF EXISTS
    freelance_auth.users;


CREATE TABLE IF NOT EXISTS freelance_auth.users
(
    id         uuid                     NOT NULL,
    username   varchar(255)             NOT NULL,
    password   varchar(255)             NOT NULL,
    email      varchar(255)             NOT NULL,
    role       varchar(255)             NOT NULL,
    status     varchar(255)             NOT NULL,
    created_at timestamp with time zone NOT NULL,
    updated_at timestamp with time zone NOT NULL
);



INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10000',
        'admin',
        'admin',
        'admin@mail.ru',
        'ADMIN',
        'ACTIVE',
        now(),
        now());


INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10001',
        'customer',
        'customer',
        'customer@mail.ru',
        'USER',
        'ACTIVE',
        now(),
        now());


INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10002',
        'executor',
        'executor',
        'executor@mail.ru',
        'USER',
        'ACTIVE',
        now(),
        now());


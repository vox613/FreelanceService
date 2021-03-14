-- Скрипт для заполнения таблиц с возможными статусами и ролями пользователей/заданий/договоров

INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10000',
        'admin',
        '$2a$10$S1akOqBB7iSHrCnaDIAobeO/EksRXkGlwand3MLJvNKFq/7IcVQH6',
        'admin@mail.ru',
        'ADMIN',
        'ACTIVE',
        now(),
        now());


INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10001',
        'customer',
        '$2a$10$UjAqwt5mFEnfZga3ThgO3uCg0HOAKDtlGRPi655QgoG7sl0EqHa.m',
        'customer@mail.ru',
        'USER',
        'ACTIVE',
        now(),
        now());


INSERT INTO freelance_auth.users(id, username, password, email, role, status, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10002',
        'executor',
        '$2a$10$l7oXTYFdgz2naG.NvHAZseMCNH3bc.LNyhq43eX3Ly.fXxlaQD4wK',
        'executor@mail.ru',
        'USER',
        'ACTIVE',
        now(),
        now());

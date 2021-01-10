-- Скрипт для заполнения таблиц с возможными статусами и ролями пользователей/заданий/договоров

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO user_roles (id, value, created_at, updated_at)
VALUES (uuid_generate_v1(), 'ADMIN', now(), now()),
       (uuid_generate_v1(), 'CUSTOMER', now(), now()),
       (uuid_generate_v1(), 'EXECUTOR', now(), now());


INSERT INTO user_statuses (id, value, description, created_at, updated_at)
VALUES (uuid_generate_v1(), 'NOT_EXIST', 'Пользователя не существует', now(), now()),
       (uuid_generate_v1(), 'CREATED', 'Создан', now(), now()),
       (uuid_generate_v1(), 'BLOCKED', 'Заблокирован', now(), now()),
       (uuid_generate_v1(), 'ACTIVE', 'Активен', now(), now());

INSERT INTO task_statuses (id, value, description, created_at, updated_at)
VALUES (uuid_generate_v1(), 'REGISTERED', 'Задание зарегистрировано', now(), now()),
       (uuid_generate_v1(), 'IN_PROGRESS', 'Задание на выполнении', now(), now()),
       (uuid_generate_v1(), 'ON_CHECK', 'Задание на проверке', now(), now()),
       (uuid_generate_v1(), 'ON_FIX', 'Задание на исправлении', now(), now()),
       (uuid_generate_v1(), 'DONE', 'Задание выполнено', now(), now()),
       (uuid_generate_v1(), 'CANCELED', 'Задание отменено', now(), now());

INSERT INTO contract_statuses (id, value, description, created_at, updated_at)
VALUES (uuid_generate_v1(), 'TERMINATED', 'Договор расторгнут', now(), now()),
       (uuid_generate_v1(), 'PAID', 'Договор оплачен', now(), now()),
       (uuid_generate_v1(), 'DONE', 'Договор исполнен', now(), now());


INSERT INTO users(id, first_name, last_name, second_name, login, password, email, phone_number, role_id, status_id,
                  wallet, created_at, updated_at)
SELECT uuid_generate_v1(),
       'admin',
       'admin',
       'admin',
       'admin',
       'admin',
       'admin@mail.ru',
       '81234567890',
       user_roles.id,
       user_statuses.id,
       1000,
       now(),
       now()
FROM user_roles, user_statuses
WHERE user_roles.value = 'ADMIN' AND user_statuses.value = 'ACTIVE';
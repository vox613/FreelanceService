-- Скрипт для заполнения таблиц с возможными статусами и ролями пользователей/заданий/договоров


INSERT INTO freelance.client_roles (id, value, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b9fa00', 'ADMIN', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa01', 'CUSTOMER', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa02', 'EXECUTOR', now(), now());

INSERT INTO freelance.client_statuses (id, value, description, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b9fa03', 'NOT_EXIST', 'Пользователя не существует', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa04', 'CREATED', 'Создан', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa05', 'BLOCKED', 'Заблокирован', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa06', 'ACTIVE', 'Активен', now(), now());


INSERT INTO freelance.task_statuses (id, value, description, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b9fa07', 'REGISTERED', 'Задание зарегистрировано', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa08', 'IN_PROGRESS', 'Задание на выполнении', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa09', 'ON_CHECK', 'Задание на проверке', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa10', 'ON_FIX', 'Задание на исправлении', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa11', 'DONE', 'Задание выполнено', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa12', 'CANCELED', 'Задание отменено', now(), now());

INSERT INTO freelance.contract_statuses (id, value, description, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b9fa13', 'TERMINATED', 'Договор расторгнут', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa14', 'PAID', 'Договор оплачен', now(), now()),
       ('be363ce8-85f2-4d02-90ce-cb3738b9fa15', 'DONE', 'Договор исполнен', now(), now());


INSERT INTO freelance.clients(id, first_name, last_name, second_name, email, phone_number, role_id, status_id,
                  wallet, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10000',
        'admin',
        'admin',
        'admin',
        'admin@mail.ru',
        '81234567890',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa00',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa06',
        1000,
        now(),
        now());


INSERT INTO freelance.clients(id, first_name, last_name, second_name, email, phone_number, role_id, status_id,
                  wallet, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10001',
        'customer',
        'customer',
        'customer',
        'customer@mail.ru',
        '88888888810',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa01',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa06',
        10000,
        now(),
        now());


INSERT INTO freelance.clients(id, first_name, last_name, second_name, email, phone_number, role_id, status_id,
                  wallet, created_at, updated_at)
VALUES ('be363ce8-85f2-4d02-90ce-cb3738b10002',
        'executor',
        'executor',
        'executor',
        'executor@mail.ru',
        '88888888811',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa02',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa06',
        0,
        now(),
        now());

--
-- Формирование первого заказа - выполненного
--
INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c000',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        'title_1',
        'description_1',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-01 22:23:33+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa11',
        500,
        'task_decision_1');


INSERT INTO freelance.contract(id, customer_id, executor_id, created_at, task_id, contract_status_id, updated_at)
VALUES ('5877195a-5097-4d6c-b272-9705e3c30100',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        now(),
        '32e7c56a-7f3c-49af-a08e-d04b2e91c000',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa15',
        now());


--
-- Формирование второго заказа - выполненного
--
INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c001',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        'title_2',
        'description_2',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-02 12:50:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa11',
        5000,
        'task_decision_2');


INSERT INTO freelance.contract(id, customer_id, executor_id, created_at, task_id, contract_status_id, updated_at)
VALUES ('5877195a-5097-4d6c-b272-9705e3c30101',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        now(),
        '32e7c56a-7f3c-49af-a08e-d04b2e91c001',
        'be363ce8-85f2-4d02-90ce-cb3738b9fa15',
        now());



--
-- Формирование заданий разных статусов для заказчика
--
-- CANCELLED
INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c002',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        'title_3',
        'description_3',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-03 13:00:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa12',
        50,
        NULL);

-- REGISTERED
INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c003',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        NULL,
        'title_4',
        'description_4',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-03 15:00:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa07',
        1000,
        NULL);

INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c004',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        NULL,
        'title_5',
        'description_5',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-03 16:00:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa07',
        1500,
        NULL);

INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c005',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        NULL,
        'title_6',
        'description_6',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-03 17:00:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa07',
        2000,
        NULL);


-- IN_PROGRESS
INSERT INTO freelance.task(id, customer_id, executor_id, title, description, created_at, task_completion_date, updated_at,
                 task_status_id, price, task_decision)
VALUES ('32e7c56a-7f3c-49af-a08e-d04b2e91c006',
        'be363ce8-85f2-4d02-90ce-cb3738b10001',
        'be363ce8-85f2-4d02-90ce-cb3738b10002',
        'title_7',
        'description_7',
        now(),
        TIMESTAMP WITH TIME ZONE '2021-05-03 18:00:00+03',
        now(),
        'be363ce8-85f2-4d02-90ce-cb3738b9fa08',
        100,
        '');
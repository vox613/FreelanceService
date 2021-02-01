-- Скрипт для добавления в таблицу данных базовой валюты

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO exchange_rates (id, num_code, char_code, nominal, name, current_value, previous_value , created_at, updated_at)
VALUES ('R01117', 643, 'RUB', 1, 'Российский рубль', 1, 1, now(), now());


-- ------------------------------------------------------ --
-- Данные для тестирования репозиториев значений (values) --
-- ------------------------------------------------------ --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Название каталога');


-- --------------------------------------------- --
-- --------------------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "folder_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(1, 1, (SELECT "id" FROM "empty_container"), NULL, 'ROOT', 'file/type', '2024-06-25T19:03:57.045Z'),
(2, 1, (SELECT "id" FROM "empty_container"), 1,    'name', 'file/type', '2024-06-25T19:03:57.045Z');


-- ----------------------------------- --
-- ----------------------------------- --
INSERT INTO "values" ("id")  OVERRIDING SYSTEM VALUE
VALUES (121),(122),(123),(124),(125),(126),(127),(128),(129),(130),(131);

ALTER SEQUENCE "values_id_seq" RESTART WITH 137;


-- ------------------- --
-- ------------------- --
INSERT INTO "integer_values"    ("id", "value")           VALUES (121, 32452840);
INSERT INTO "double_values"     ("id", "value")           VALUES (122, 3.14E-9);
INSERT INTO "bigdecimal_values" ("id", "value")           VALUES (123, 32452840.456);
INSERT INTO "string_values"     ("id", "value")           VALUES (124, 'Строковое значение');
INSERT INTO "text_values"       ("id", "value", "vector") VALUES (125, 'Очень длинный текст...', to_tsvector('Очень длинный текст...'));
INSERT INTO "date_values"       ("id", "value")           VALUES (126, '2024-06-25');
INSERT INTO "datetime_values"   ("id", "value")           VALUES (127, '2024-06-25T19:03:57.045Z');
INSERT INTO "bytes_values"      ("id", "value")           VALUES (128, 'aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI=');
INSERT INTO "uuid_values"       ("id", "value")           VALUES (129, 'bb5d11b6-3b10-414d-9d11-b63b10714dd2');
INSERT INTO "json_values"       ("id", "value")           VALUES (130, '{"figure": [true, "square"], "tags": {"a": 1, "b": null}}'::JSONB);
INSERT INTO "file_id_values"    ("id", "file_id")         VALUES (131, 1);

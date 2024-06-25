-- -------------------- --
-- Добавление картотеки --
-- -------------------- --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Название каталога');

-- --------------------------------------------- --
-- Добавление файла в файловую систему картотеки --
-- --------------------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "folder_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(1, 1, (SELECT "id" FROM "empty_container"), NULL, 'ROOT', 'file/type', '2024-06-25T19:03:57.045Z'),
(2, 1, (SELECT "id" FROM "empty_container"), 1,    'name', 'file/type', '2024-06-25T19:03:57.045Z');


-- ----------------------------------- --
-- Добавление идентификаторов значений --
-- ----------------------------------- --
INSERT INTO "values" ("id")  OVERRIDING SYSTEM VALUE
VALUES (21),(22),(23),(24),(25),(26),(27),(28),(29),(30),(31);

ALTER SEQUENCE "values_id_seq" RESTART WITH 37;


-- ------------------- --
-- Добавление значений --
-- ------------------- --
INSERT INTO "integer_values" ("id", "value") VALUES (21, 32452840);
INSERT INTO "double_values" ("id", "value") VALUES (22, 3.14E-9);
INSERT INTO "bigdecimal_values" ("id", "value") VALUES (23, 32452840.456);
INSERT INTO "string_values" ("id", "value") VALUES (24, 'Строковое значение');
INSERT INTO "text_values" ("id", "value", "vector") VALUES (25, 'Очень длинный текст...', to_tsvector('Очень длинный текст...'));
INSERT INTO "date_values" ("id", "value") VALUES (26, '2024-06-25');
INSERT INTO "datetime_values" ("id", "value") VALUES (27, '2024-06-25T19:03:57.045Z');
INSERT INTO "bytes_values" ("id", "value") VALUES (28, 'aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI=');
INSERT INTO "uuid_values" ("id", "value") VALUES (29, 'bb5d11b6-3b10-414d-9d11-b63b10714dd2');
INSERT INTO "json_values" ("id", "value") VALUES (30, '{"figure": [true, "square"], "tags": {"a": 1, "b": null}}'::JSONB);
INSERT INTO "file_id_values" ("id", "file_id") VALUES (31, 1);

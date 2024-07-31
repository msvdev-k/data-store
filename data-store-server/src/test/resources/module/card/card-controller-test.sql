-- ------------------------------------------------------------- --
-- Данные для тестирования API управления карточками из картотек --
-- ------------------------------------------------------------- --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Каталог 1');


INSERT INTO "user_authorities" ("catalog_id", "user_uuid", "authority")
VALUES (1, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'MASTER');




-- ------------------------- --
-- ------------------------- --
INSERT INTO "fields" ("id", "catalog_id", "order", "name", "type_id")
OVERRIDING SYSTEM VALUE
VALUES
(21, 1, 1,  'null',        1 ),
(22, 1, 2,  'integer',     2 ),
(23, 1, 3,  'double',      3 ),
(24, 1, 4,  'big_decimal', 4 ),
(25, 1, 5,  'string',      5 ),
(26, 1, 6,  'text',        6 ),
(27, 1, 7,  'date',        7 ),
(28, 1, 8,  'datetime',    8 ),
(29, 1, 9,  'boolean',     9 ),
(30, 1, 10, 'bytes',       10),
(31, 1, 11, 'uuid',        11),
(32, 1, 12, 'json',        12),
(33, 1, 13, 'file_id',     14);




-- ------------------- --
-- ------------------- --
INSERT INTO "cards" ("id", "catalog_id") OVERRIDING SYSTEM VALUE
VALUES
(41, 1),
(42, 1),
(43, 1);

ALTER SEQUENCE "cards_id_seq" RESTART WITH 57;




-- ----------------------------------- --
-- ----------------------------------- --
INSERT INTO "values" ("id") OVERRIDING SYSTEM VALUE
VALUES (251),(252),(253),(254),(255),(256),(257),(258),(259),(260),(261),(262),(263);




-- ---------------- --
-- ---------------- --
INSERT INTO "tags" ("card_id", "field_id", "value_id") OVERRIDING SYSTEM VALUE
VALUES
(41, 21,  -1),
(41, 22, 252),
(41, 23, 253),
(41, 24, 254),
(41, 25, 255),
(41, 26, 256),
(41, 27, 257),
(41, 28, 258),
(41, 29,   1),
(41, 30, 260),
(41, 31, 261),
(41, 32, 262),
(41, 33, 263),

(42, 22, 252),
(42, 23, 253),
(42, 24, 254),
(42, 25, 255),
(42, 26, 256),
(42, 27, 257),
(42, 28, 258),
(42, 29,   0);




-- --------------------------------------------- --
-- --------------------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "folder_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(1, 1, (SELECT "id" FROM "empty_container"), NULL, 'ROOT', 'file/type', '2024-06-25T19:03:57.045Z'),
(2, 1, (SELECT "id" FROM "empty_container"), 1,    'name', 'file/type', '2024-06-25T19:03:57.045Z');




-- ------------------- --
-- ------------------- --
INSERT INTO "integer_values"    ("id", "value")           VALUES (252, 32452840);
INSERT INTO "double_values"     ("id", "value")           VALUES (253, 3.14E-9);
INSERT INTO "bigdecimal_values" ("id", "value")           VALUES (254, 32452840.456);
INSERT INTO "string_values"     ("id", "value")           VALUES (255, 'Строковое значение');
INSERT INTO "text_values"       ("id", "value", "vector") VALUES (256, 'Очень длинный текст...', to_tsvector('Очень длинный текст...'));
INSERT INTO "date_values"       ("id", "value")           VALUES (257, '2024-06-25');
INSERT INTO "datetime_values"   ("id", "value")           VALUES (258, '2024-06-25T19:03:57Z');
INSERT INTO "bytes_values"      ("id", "value")           VALUES (260, 'aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI=');
INSERT INTO "uuid_values"       ("id", "value")           VALUES (261, 'bb5d11b6-3b10-414d-9d11-b63b10714dd2');
INSERT INTO "json_values"       ("id", "value")           VALUES (262, '{"tags": {"a": 1, "b": null}, "figure": [true, "square"]}'::JSONB);
INSERT INTO "file_id_values"    ("id", "file_id")         VALUES (263, 1);


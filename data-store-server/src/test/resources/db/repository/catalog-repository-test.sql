-- ------------------- --
-- Добавление картотек --
-- ------------------- --
INSERT INTO "catalogs" ("id", "name", "description") OVERRIDING SYSTEM VALUE
VALUES
(1, 'Название каталога', 'Описание каталога'),
(2, 'Книги', 'Каталог книг'),
(3, 'Books', 'Book catalog'),
(4, 'Рецепты', NULL);

ALTER SEQUENCE "catalogs_id_seq" RESTART WITH 37;

-- ----------------------------------- --
-- Добавление полномочия пользователей --
-- ----------------------------------- --
INSERT INTO "user_authorities" ("catalog_id", "user_uuid", "authority") VALUES
(1, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'MASTER'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'GRANT_AUTHORITY'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'READING'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'WRITING'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'DELETING'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'FIELD_TEMPLATE_WRITING'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'FIELD_TEMPLATE_DELETING'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'FILE_UPLOAD'),
(2, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41272', 'FILE_DOWNLOAD'),

(2, '42b16c80-7987-462b-b16c-807987062be1', 'MASTER'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'GRANT_AUTHORITY'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'READING'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'WRITING'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'DELETING'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'FIELD_TEMPLATE_WRITING'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'FIELD_TEMPLATE_DELETING'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'FILE_UPLOAD'),
(1, '42b16c80-7987-462b-b16c-807987062be1', 'FILE_DOWNLOAD'),

(1, '64f25d2f-953f-4605-b25d-2f953f260558', 'READING');

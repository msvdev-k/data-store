-- -------------------- --
-- Добавление картотеки --
-- -------------------- --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Название каталога 1'), (2, 'Название каталога 2'), (3, 'Название каталога 3');


-- ------------------- --
-- Добавление карточек --
-- ------------------- --
INSERT INTO "cards" ("id", "catalog_id")
OVERRIDING SYSTEM VALUE
VALUES
(11, 1),
(12, 1),
(13, 1),
(14, 1),
(15, 1),
(16, 2),
(17, 2);

ALTER SEQUENCE "cards_id_seq" RESTART WITH 37;

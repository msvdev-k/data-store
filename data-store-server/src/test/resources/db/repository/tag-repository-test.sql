-- -------------------- --
-- Добавление картотеки --
-- -------------------- --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Название каталога 1'), (2, 'Название каталога 2'), (3, 'Название каталога 3');


-- ------------------------- --
-- Добавление полей карточек --
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
-- Добавление карточки --
-- ------------------- --
INSERT INTO "cards" ("id", "catalog_id") OVERRIDING SYSTEM VALUE
VALUES (41, 1);


-- ----------------------------------- --
-- Добавление идентификаторов значений --
-- ----------------------------------- --
INSERT INTO "values" ("id") OVERRIDING SYSTEM VALUE
VALUES (51),(52),(53),(54),(55),(56),(57),(58),(59),(60),(61),(62),(63),(67);


-- ---------------- --
-- Добавление тегов --
-- ---------------- --
INSERT INTO "tags" ("card_id", "field_id", "value_id") OVERRIDING SYSTEM VALUE
VALUES
(41, 21, 51),
(41, 22, 52),
(41, 23, 53),
(41, 24, 54),
(41, 25, 55),
(41, 26, 56),
(41, 27, 57),
(41, 28, 58),
(41, 29, 59),
(41, 30, 60),
(41, 31, 61),
(41, 32, 62),
(41, 33, 63);

-- -------------------- --
-- Добавление картотеки --
-- -------------------- --
INSERT INTO "catalogs" ("id", "name") OVERRIDING SYSTEM VALUE
VALUES (1, 'Название каталога');


-- ------------------------- --
-- Добавление полей карточек --
-- ------------------------- --
INSERT INTO "fields" ("id", "catalog_id", "order", "name", "description", "type_id", "format")
OVERRIDING SYSTEM VALUE
VALUES
(11, 1, 1,  'null',        NULL,             1,  NULL),
(12, 1, 2,  'integer',     'Long',           2,  NULL),
(13, 1, 3,  'double',      'Double',         3,  NULL),
(14, 1, 4,  'big_decimal', 'BigDecimal',     4,  NULL),
(15, 1, 5,  'string',      'String',         5,  NULL),
(16, 1, 6,  'text',        'String',         6,  NULL),
(17, 1, 7,  'date',        'LocalDate',      7,  NULL),
(18, 1, 8,  'datetime',    'OffsetDateTime', 8,  NULL),
(19, 1, 9,  'boolean',     'Boolean',        9,  NULL),
(20, 1, 10, 'bytes',       'String',         10, NULL),
(21, 1, 11, 'uuid',        'UUID',           11, NULL),
(22, 1, 12, 'json',        'String',         12, NULL),
(23, 1, 13, 'file_id',     NULL,             14, NULL);

ALTER SEQUENCE "fields_id_seq" RESTART WITH 37;

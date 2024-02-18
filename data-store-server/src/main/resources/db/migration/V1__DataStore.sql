-- ------------------------------------------------------- --
-- Схема хранилища данных на языке SQL диалекта PostgreSQL --
-- ------------------------------------------------------- --


-- ---------------- --
-- Таблица картотек --
-- ---------------- --
CREATE TABLE "catalogs" (
    "id"            BIGINT      PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "name"          VARCHAR(64) NOT NULL,   -- Название картотеки
    "description"   TEXT        NULL        -- Подробное описание картотеки
);



-- --------------------------------------------------------- --
-- Таблица полномочий пользователей при работе с картотеками --
-- --------------------------------------------------------- --
CREATE TABLE "user_authorities" (
    "catalog_id" BIGINT      NOT NULL, -- Идентификатор картотеки
    "user_uuid"  UUID        NOT NULL, -- Идентификатор пользователя
    "authority"  VARCHAR(64) NOT NULL, -- Полномочие пользователя

    PRIMARY KEY ("catalog_id", "user_uuid", "authority")
);

ALTER TABLE "user_authorities" ADD CONSTRAINT "user_authorities_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- ------------------------------------------------------ --
-- Таблица типов данных ассоциированных с полями карточек --
-- ------------------------------------------------------ --
CREATE TABLE "field_types" (
    "id"          INTEGER      PRIMARY KEY, -- Идентификатор типа данных
    "type"        VARCHAR(32)  NOT NULL,    -- Обозначение типа данных
    "description" VARCHAR(256) NULL,        -- Описание
    "pg_type"     VARCHAR(32)  NULL,        -- Соответствующий тип данных PostgreSQL
    "java_type"   VARCHAR(32)  NULL         -- Соответствующий тип данных Java
);

INSERT INTO "field_types" ("id", "type", "description", "pg_type", "java_type") VALUES
(1,  'NULL',        'Никаких данных с полем не ассоциированно',                   NULL,                       NULL             ),
(2,  'INTEGER',     'Целое число размером 8 байт',                               'BIGINT',                   'Long'            ),
(3,  'DOUBLE',      'Вещественное число размером 8 байт',                        'DOUBLE PRECISION',         'Double'          ),
(4,  'BIG_DECIMAL', 'Вещественное число  с произвольной точностью',              'NUMERIC',                  'BigDecimal'      ),
(5,  'STRING',      'Строка длиной до 1 Гб',                                     'TEXT',                     'String'          ),
(6,  'TEXT',        'Длинные строки с поддержкой полнотекстового поиска',        'TSVECTOR',                 'String'          ),
(7,  'DATE',        'Дата с точностью до 1 дня',                                 'DATE',                     'LocalDate'       ),
(8,  'DATETIME',    'Дата и время с часовым поясом точностью до 1 микросекунды', 'TIMESTAMP WITH TIME ZONE', 'OffsetDateTime'  ),
(9,  'BOOLEAN',     'Логическое значение (TRUE, FALSE)',                         'BOOLEAN',                  'Boolean'         ),
(10, 'BYTES',       'Строка с двоичным представлением данных в формате Base64',  'TEXT',                     'String'          ),
(11, 'UUID',        'Всемирно уникальный идентификатор',                         'UUID',                     'UUID'            ),
(12, 'JSON',        'Запись объекта JavaScript',                                 'JSONB',                    'String'          );



-- ---------------------- --
-- Таблица полей карточек --
-- ---------------------- --
CREATE TABLE "fields" (
    "id"          BIGINT      PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "catalog_id"  BIGINT      NOT NULL,   -- Идентификатор картотеки, которой принадлежит поле карточки
    "order"       INTEGER     NOT NULL,   -- Порядковый номер поля в карточке
    "name"        VARCHAR(32) NOT NULL,   -- Название поля
    "description" TEXT        NULL,       -- Подробное описание оля
    "type_id"     INTEGER     NOT NULL,   -- Тип данных, определяющий способ хранения, сортировки и сравнения в БД
    "format"      VARCHAR(64) NULL        -- Формат интерпретации данных клиентом
);

ALTER TABLE "fields" ADD CONSTRAINT "fields_unique_name" UNIQUE ("catalog_id", "name");

ALTER TABLE "fields" ADD CONSTRAINT "fields_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "fields" ADD CONSTRAINT "fields_type_fk"
    FOREIGN KEY ("type_id") REFERENCES "field_types" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;



-- -------------------------------------- --
-- Таблица картотечных записей (карточек) --
-- -------------------------------------- --
CREATE TABLE "cards" (
    "id"         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "catalog_id" BIGINT NOT NULL    -- Идентификатор картотеки которой принадлежит карточка
);

ALTER TABLE "cards" ADD CONSTRAINT "cards_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;




-- -------------------------------------------------- --
-- Таблицы значений ассоциированных с полями карточек --
-- -------------------------------------------------- --
CREATE TABLE "values" (
    "id" BIGINT PRIMARY KEY -- Идентификатор значения
);

CREATE SEQUENCE "values_id_seq" AS BIGINT START 100 OWNED BY "values"."id";
ALTER TABLE "values" ALTER COLUMN "id" SET DEFAULT nextval('values_id_seq');


-- Constant values ---
-- -------------------
INSERT INTO "values" ("id") VALUES
(-1), -- Null
( 0), -- False
( 1); -- True


-- Integer -----------
-- -------------------
CREATE TABLE "integer_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" BIGINT NOT NULL     -- Значение
);

ALTER TABLE "integer_values" ADD CONSTRAINT "integer_values_unique_value" UNIQUE ("value");
ALTER TABLE "integer_values" ADD CONSTRAINT "integer_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- Double ------------
-- -------------------
CREATE TABLE "double_values" (
    "id"    BIGINT           PRIMARY KEY, -- Идентификатор значения
    "value" DOUBLE PRECISION NOT NULL     -- Значение
);

ALTER TABLE "double_values" ADD CONSTRAINT "double_values_unique_value" UNIQUE ("value");
ALTER TABLE "double_values" ADD CONSTRAINT "double_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- BigDecimal --------
-- -------------------
CREATE TABLE "bigdecimal_values" (
    "id"    BIGINT  PRIMARY KEY, -- Идентификатор значения
    "value" NUMERIC NOT NULL     -- Значение
);

ALTER TABLE "bigdecimal_values" ADD CONSTRAINT "bigdecimal_values_unique_value" UNIQUE ("value");
ALTER TABLE "bigdecimal_values" ADD CONSTRAINT "bigdecimal_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- String ------------
-- -------------------
CREATE TABLE "string_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" TEXT   NOT NULL     -- Значение
);

ALTER TABLE "string_values" ADD CONSTRAINT "string_values_unique_value" UNIQUE ("value");
ALTER TABLE "string_values" ADD CONSTRAINT "string_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- Text --------------
-- -------------------
CREATE TABLE "text_values" (
    "id"     BIGINT   PRIMARY KEY, -- Идентификатор значения
    "value"  TEXT     NOT NULL,    -- Значение (текст)
    "vector" TSVECTOR NOT NULL     -- Отсортированный список неповторяющихся лексем
);

ALTER TABLE "text_values" ADD CONSTRAINT "text_values_unique_value" UNIQUE ("value");
ALTER TABLE "text_values" ADD CONSTRAINT "text_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- Date --------------
-- -------------------
CREATE TABLE "date_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" DATE   NOT NULL     -- Значение
);

ALTER TABLE "date_values" ADD CONSTRAINT "date_values_unique_value" UNIQUE ("value");
ALTER TABLE "date_values" ADD CONSTRAINT "date_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- DateTime ----------
-- -------------------
CREATE TABLE "datetime_values" (
    "id"    BIGINT                   PRIMARY KEY, -- Идентификатор значения
    "value" TIMESTAMP WITH TIME ZONE NOT NULL     -- Значение
);

ALTER TABLE "datetime_values" ADD CONSTRAINT "datetime_values_unique_value" UNIQUE ("value");
ALTER TABLE "datetime_values" ADD CONSTRAINT "datetime_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- Bytes -------------
-- -------------------
CREATE TABLE "bytes_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" TEXT   NOT NULL     -- Значение
);

ALTER TABLE "bytes_values" ADD CONSTRAINT "bytes_values_unique_value" UNIQUE ("value");
ALTER TABLE "bytes_values" ADD CONSTRAINT "bytes_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- UUID --------------
-- -------------------
CREATE TABLE "uuid_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" UUID   NOT NULL     -- Значение
);

ALTER TABLE "uuid_values" ADD CONSTRAINT "uuid_values_unique_value" UNIQUE ("value");
ALTER TABLE "uuid_values" ADD CONSTRAINT "uuid_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;


-- Json --------------
-- -------------------
CREATE TABLE "json_values" (
    "id"    BIGINT PRIMARY KEY, -- Идентификатор значения
    "value" JSONB  NOT NULL     -- Значение
);

ALTER TABLE "json_values" ADD CONSTRAINT "json_values_unique_value" UNIQUE ("value");
ALTER TABLE "json_values" ADD CONSTRAINT "json_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- -------------------------------------------------------------------------------------------------- --
-- Таблица тегов (меток) карточек. Тег или метка - это уникальная для карточки связка поля и значения --
-- -------------------------------------------------------------------------------------------------- --
CREATE TABLE "tags" (
    "card_id"  BIGINT NOT NULL, -- Идентификатор карточки
    "field_id" BIGINT NOT NULL, -- Идентификатор поля
    "value_id" BIGINT NOT NULL, -- Идентификатор значения ассоциированного с полем

    PRIMARY KEY ("card_id", "field_id", "value_id")
);

ALTER TABLE "tags" ADD CONSTRAINT "tags_card_fk"
    FOREIGN KEY ("card_id") REFERENCES "cards" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "tags" ADD CONSTRAINT "tags_field_template_fk"
    FOREIGN KEY ("field_id") REFERENCES "fields" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "tags" ADD CONSTRAINT "tags_field_value_fk"
    FOREIGN KEY ("value_id") REFERENCES "values" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

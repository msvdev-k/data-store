-- ------------------------------------------------------- --
-- Схема хранилища данных на языке SQL диалекта PostgreSQL --
-- ------------------------------------------------------- --


-- --------------------------- --
-- Таблица дескрипторов файлов --
-- --------------------------- --
CREATE TABLE "file_handles" (
    "id"              BIGINT       PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "sha256"          VARCHAR(64)  NOT NULL,   -- Hash-сумма файла hex(sha256(content))
    "mime_type"       VARCHAR(128) NOT NULL,   -- Тип файла
    "size"            BIGINT       NOT NULL,   -- Размер файла (байт)
    "chunk_count"     INTEGER      NOT NULL,   -- Количество фрагментов, на которое разбит файл
    "chunk_size"      INTEGER      NOT NULL,   -- Размер фрагментов файла (байт)
    "last_chunk_size" INTEGER      NOT NULL    -- Размер последнего фрагмента файла (байт)
);

ALTER TABLE "file_handles" ADD CONSTRAINT "file_handles_unique_sha256" UNIQUE ("sha256");



-- ---------------------------------- --
-- Таблица бинарных фрагментов файлов --
-- ---------------------------------- --
CREATE TABLE "chunks" (
    "id"      BIGINT  PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "size"    INTEGER NOT NULL, -- Размер фрагмента файла (байт)
    "content" TEXT    NOT NULL  -- Содержимое фрагмента файла в виде строки Base64
);



-- ----------------------------------------------- --
-- Таблица связи фрагментов с дескрипторами файлов --
-- ----------------------------------------------- --
CREATE TABLE "file_chunks" (
    "file_handle_id" BIGINT  NOT NULL, -- Идентификатор дескриптора файла
    "chunk_id"       BIGINT  NOT NULL, -- Идентификатор фрагмента файла
    "number"         INTEGER NOT NULL, -- Порядковый номер фрагмента (нумерация начинается с единицы, т.е. 1,2,3,4,...)

    PRIMARY KEY ("file_handle_id", "chunk_id")
);

ALTER TABLE "file_chunks" ADD CONSTRAINT "file_chunks_unique_number" UNIQUE ("file_handle_id", "number");
ALTER TABLE "file_chunks" ADD CONSTRAINT "file_chunks_positive_number" CHECK ("number" > 0);
ALTER TABLE "file_chunks" ADD CONSTRAINT "file_chunks_file_handle_fk"
    FOREIGN KEY ("file_handle_id") REFERENCES "file_handles" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "file_chunks" ADD CONSTRAINT "file_chunks_catalog_fk"
    FOREIGN KEY ("chunk_id") REFERENCES "chunks" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- --------------------------------------------------- --
-- Таблица файлов картотек (файловая система картотек) --
-- --------------------------------------------------- --
CREATE TABLE "files" (
    "id"             BIGINT                   PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "catalog_id"     BIGINT                   NOT NULL, -- Идентификатор картотеки, которой принадлежит файл/каталог
    "folder_id"      BIGINT                   NULL,     -- Идентификатор каталога, которому принадлежит файл/каталог
    "file_handle_id" BIGINT                   NOT NULL, -- Идентификатор дескриптора файла/каталога
    "name"           VARCHAR(128)             NOT NULL, -- Название файла/каталога
    "create_date"    TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время создания файла/каталога

    "uname"          VARCHAR(128) GENERATED ALWAYS AS (upper("name")) STORED
);

ALTER TABLE "files" ADD CONSTRAINT "files_folder_check" CHECK  (("folder_id" IS NULL AND "uname" = 'ROOT') OR "folder_id" IS NOT NULL);
ALTER TABLE "files" ADD CONSTRAINT "files_unique_name"  UNIQUE NULLS NOT DISTINCT ("catalog_id", "folder_id", "uname");
ALTER TABLE "files" ADD CONSTRAINT "files_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "files" ADD CONSTRAINT "files_file_handle_fk"
    FOREIGN KEY ("file_handle_id") REFERENCES "file_handles" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "files" ADD CONSTRAINT "files_folder_fk"
    FOREIGN KEY ("folder_id") REFERENCES "files" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;



-- --------------------------------------------------------- --
-- Глобальный дескриптор каталога файловой системы картотеки --
-- --------------------------------------------------------- --
INSERT INTO "file_handles" ("sha256", "mime_type", "size", "chunk_count", "chunk_size", "last_chunk_size") VALUES
('e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 'inode/directory', 0, 0, 0, 0);

CREATE VIEW "folder_handle" AS (SELECT * FROM "file_handles" WHERE "sha256" = 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855');


-- ----------------------------------------------- --
-- Тип данных ассоциированных с дескриптором файла --
-- ----------------------------------------------- --
INSERT INTO "field_types" ("id", "type", "description", "pg_type", "java_type") VALUES
(14, 'FILE', 'Идентификатор файла', 'BIGINT', 'Long');



-- ------------------------------------------------ --
-- Таблица файлов ассоциированных с полями карточек --
-- ------------------------------------------------ --
CREATE TABLE "file_values" (
    "id"      BIGINT      PRIMARY KEY, -- Идентификатор значения
    "file_id" BIGINT      NOT NULL,    -- Идентификатор файла
    "name"    VARCHAR(64) NULL         -- Краткое название файла, привязанного к карточке

);

ALTER TABLE "file_values" ADD CONSTRAINT "file_values_unique_file_id" UNIQUE ("file_id");
ALTER TABLE "file_values" ADD CONSTRAINT "file_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "file_values" ADD CONSTRAINT "file_values_value_fk"
    FOREIGN KEY ("file_id") REFERENCES "files" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;



-- ---------------------------------------- --
-- Таблица сессий выгрузки файлов на сервер --
-- ---------------------------------------- --
CREATE TABLE "upload_sessions" (
    "id"              BIGINT  PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "state"           VARCHAR(32)         NOT NULL, -- Состояние сессии (UPLOAD, PROCESSING, ERROR)
    "sha256"          VARCHAR(64)         NOT NULL, -- Hash-сумма файла hex(sha256(content))
    "size"            BIGINT              NOT NULL, -- Размер файла (байт)
    "chunk_count"     INTEGER             NOT NULL, -- Количество фрагментов, на которое разбит файл
    "chunk_size"      INTEGER             NOT NULL, -- Размер фрагментов файла (байт)
    "last_chunk_size" INTEGER             NOT NULL  -- Размер последнего фрагмента файла (байт)
);

ALTER TABLE "upload_sessions" ADD CONSTRAINT "upload_sessions_unique_sha256" UNIQUE ("sha256");



-- ----------------------------------------- --
-- Таблица сессий выгрузки фрагментов файлов --
-- ----------------------------------------- --
CREATE TABLE "upload_chunks" (
    "upload_session_id" BIGINT                   NOT NULL, -- Идентификатор сессии выгрузки файла
    "chunk_id"          BIGINT                   NOT NULL, -- Идентификатор фрагмента файла
    "number"            INTEGER                  NOT NULL, -- Порядковый номер фрагмента
    "begin"             TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время начала сессии
    "end"               TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время окончания сессии
    "state"             VARCHAR(32)              NOT NULL, -- Состояние сессии (UPLOAD, PROCESSING, ERROR)
    "user_uuid"         UUID                     NOT NULL, -- Идентификатор пользователя открывшего сессию

    PRIMARY KEY ("upload_session_id", "chunk_id", "number")
);

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_check_date" CHECK ("begin" < "end");

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_upload_session_id_fk"
    FOREIGN KEY ("upload_session_id") REFERENCES "upload_sessions" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_chunk_fk"
    FOREIGN KEY ("chunk_id") REFERENCES "chunks" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- ---------------------------------------------------- --
-- Таблица сессий выгрузки файлов открытых в картотеках --
-- ---------------------------------------------------- --
CREATE TABLE "sessions" (
    "catalog_id"        BIGINT                   NOT NULL, -- Идентификатор картотеки в которой открывается сессия
    "upload_session_id" BIGINT                   NOT NULL, -- Идентификатор сессии выгрузки файла
    "begin"             TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время начала сессии
    "end"               TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время окончания сессии

    PRIMARY KEY ("catalog_id", "upload_session_id")
);

ALTER TABLE "sessions" ADD CONSTRAINT "sessions_check_date" CHECK ("begin" < "end");

ALTER TABLE "sessions" ADD CONSTRAINT "sessions_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "sessions" ADD CONSTRAINT "sessions_upload_session_fk"
    FOREIGN KEY ("upload_session_id") REFERENCES "upload_sessions" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;



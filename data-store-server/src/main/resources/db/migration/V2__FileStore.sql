-- ------------------------------------------------------- --
-- Схема хранилища данных на языке SQL диалекта PostgreSQL --
-- ------------------------------------------------------- --


-- ------------------------------------------------ --
-- Таблица контейнеров для хранения бинарных данных --
-- ------------------------------------------------ --
CREATE TABLE "containers" (
    "id"              BIGINT       PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "sha256"          VARCHAR(64)  NOT NULL,   -- Hash-сумма контейнера данных hex(sha256(content))
    "size"            BIGINT       NOT NULL,   -- Размер контейнера данных (байт)
    "chunk_count"     INTEGER      NOT NULL,   -- Количество фрагментов, на которое разбит контейнер
    "chunk_size"      INTEGER      NOT NULL,   -- Размер фрагментов контейнера (байт)
    "last_chunk_size" INTEGER      NOT NULL    -- Размер последнего фрагмента контейнера (байт)
);

ALTER TABLE "containers" ADD CONSTRAINT "containers_unique_sha256" UNIQUE ("sha256");



-- ------------------------------------------------------------- --
-- Таблица фрагментов бинарных данных содержащихся в контейнерах --
-- ------------------------------------------------------------- --
CREATE TABLE "chunks" (
    "id"      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "content" TEXT   NOT NULL -- Содержимое фрагмента данных контейнера в виде строки Base64
);



-- --------------------------------------------------- --
-- Таблица связей фрагментов контейнеров с заголовками --
-- --------------------------------------------------- --
CREATE TABLE "container_chunks" (
    "container_id" BIGINT  NOT NULL, -- Идентификатор контейнера
    "chunk_id"     BIGINT  NOT NULL, -- Идентификатор фрагмента данных содержащихся в контейнере
    "number"       INTEGER NOT NULL, -- Порядковый номер фрагмента (нумерация начинается с единицы, т.е. 1,2,3,4,...)

    PRIMARY KEY ("container_id", "chunk_id")
);

ALTER TABLE "container_chunks" ADD CONSTRAINT "container_chunks_unique_number"   UNIQUE ("container_id", "number");
ALTER TABLE "container_chunks" ADD CONSTRAINT "container_chunks_positive_number" CHECK  ("number" > 0);
ALTER TABLE "container_chunks" ADD CONSTRAINT "container_chunks_container_fk"
    FOREIGN KEY ("container_id") REFERENCES "containers" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "container_chunks" ADD CONSTRAINT "container_chunks_chunk_fk"
    FOREIGN KEY ("chunk_id") REFERENCES "chunks" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- ---------------------------------------------------------------- --
-- Таблица дескрипторов файлов картотек (файловая система картотек) --
-- ---------------------------------------------------------------- --
CREATE TABLE "files" (
    "id"           BIGINT                   PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "catalog_id"   BIGINT                   NOT NULL, -- Идентификатор картотеки, которой принадлежит файл/каталог
    "folder_id"    BIGINT                   NULL,     -- Идентификатор каталога, которому принадлежит файл/каталог
    "container_id" BIGINT                   NOT NULL, -- Идентификатор контейнера данных файла/каталога
    "name"         VARCHAR(128)             NOT NULL, -- Название файла/каталога
    "mime_type"    VARCHAR(128)             NOT NULL, -- Тип файла/каталога
    "create_date"  TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время создания файла/каталога

    "uname"        VARCHAR(128) GENERATED ALWAYS AS (upper("name")) STORED
);

ALTER TABLE "files" ADD CONSTRAINT "files_folder_check" CHECK  (("folder_id" IS NULL AND "uname" = '$ROOT$') OR "folder_id" IS NOT NULL);
ALTER TABLE "files" ADD CONSTRAINT "files_unique_name"  UNIQUE NULLS NOT DISTINCT ("catalog_id", "folder_id", "uname");
ALTER TABLE "files" ADD CONSTRAINT "files_catalog_fk"
    FOREIGN KEY ("catalog_id") REFERENCES "catalogs" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "files" ADD CONSTRAINT "files_container_fk"
    FOREIGN KEY ("container_id") REFERENCES "containers" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

ALTER TABLE "files" ADD CONSTRAINT "files_folder_fk"
    FOREIGN KEY ("folder_id") REFERENCES "files" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;



-- ----------------------------- --
-- Заголовок пустого контейнера  --
-- ----------------------------- --
INSERT INTO "containers" ("sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") VALUES
('e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855', 0, 0, 0, 0);

CREATE VIEW "empty_container" AS (SELECT * FROM "containers" WHERE "sha256" = 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855');


-- ----------------------------------------------- --
-- Тип данных ассоциированных с дескриптором файла --
-- ----------------------------------------------- --
INSERT INTO "value_types" ("id", "type", "description", "pg_type", "java_type") VALUES
(14, 'FILE_ID', 'Идентификатор файла', 'BIGINT', 'Long');



-- ---------------------------------------------------------------- --
-- Таблица идентификаторов файлов ассоциированных с полями карточек --
-- ---------------------------------------------------------------- --
CREATE TABLE "file_id_values" (
    "id"      BIGINT PRIMARY KEY, -- Идентификатор значения
    "file_id" BIGINT NOT NULL     -- Идентификатор файла
);

ALTER TABLE "file_id_values" ADD CONSTRAINT "file_id_values_unique_file_id" UNIQUE ("file_id");
ALTER TABLE "file_id_values" ADD CONSTRAINT "file_id_values_fk"
    FOREIGN KEY ("id") REFERENCES "values" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "file_id_values" ADD CONSTRAINT "file_id_values_value_fk"
    FOREIGN KEY ("file_id") REFERENCES "files" ("id")
    ON DELETE RESTRICT
    ON UPDATE CASCADE;



-- ------------------------------------------------- --
-- Таблица сессий выгрузки бинарных данных на сервер --
-- ------------------------------------------------- --
CREATE TABLE "upload_sessions" (
    "id"              BIGINT  PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "state"           VARCHAR(32)         NOT NULL, -- Состояние сессии (UPLOAD, PROCESSING, ERROR)
    "sha256"          VARCHAR(64)         NOT NULL, -- Hash-сумма контейнера данных hex(sha256(content))
    "size"            BIGINT              NOT NULL, -- Размер контейнера (байт)
    "chunk_count"     INTEGER             NOT NULL, -- Количество фрагментов, на которое разбит контейнер
    "chunk_size"      INTEGER             NOT NULL, -- Размер фрагментов контейнера (байт)
    "last_chunk_size" INTEGER             NOT NULL  -- Размер последнего фрагмента контейнера (байт)
);

ALTER TABLE "upload_sessions" ADD CONSTRAINT "upload_sessions_unique_sha256" UNIQUE ("sha256");



-- -------------------------------------------------------------- --
-- Таблица сессий выгрузки фрагментов контейнеров бинарных данных --
-- -------------------------------------------------------------- --
CREATE TABLE "upload_chunks" (
    "upload_session_id" BIGINT                   NOT NULL, -- Идентификатор сессии выгрузки контейнера
    "chunk_id"          BIGINT                   NOT NULL, -- Идентификатор фрагмента контейнера
    "number"            INTEGER                  NOT NULL, -- Порядковый номер фрагмента
    "user_uuid"         UUID                     NOT NULL, -- Идентификатор пользователя открывшего сессию
    "state"             VARCHAR(32)              NOT NULL, -- Состояние сессии (UPLOAD, PROCESSING, ERROR)
    "last_modified"     TIMESTAMP WITH TIME ZONE NOT NULL, -- Дата и время последнего изменения состояния сессии

    PRIMARY KEY ("upload_session_id", "chunk_id")
);

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_unique_number"   UNIQUE ("upload_session_id", "number");
ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_positive_number" CHECK  ("number" > 0);

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_upload_session_fk"
    FOREIGN KEY ("upload_session_id") REFERENCES "upload_sessions" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

ALTER TABLE "upload_chunks" ADD CONSTRAINT "upload_chunks_chunk_fk"
    FOREIGN KEY ("chunk_id") REFERENCES "chunks" ("id")
    ON DELETE CASCADE
    ON UPDATE CASCADE;

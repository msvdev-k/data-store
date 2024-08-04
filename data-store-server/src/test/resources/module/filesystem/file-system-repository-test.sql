-- -------------------------------------------- --
-- Данные для тестирования FileSystemRepository --
-- -------------------------------------------- --
INSERT INTO "catalogs" ("id", "name", "description") OVERRIDING SYSTEM VALUE
VALUES
(110, 'Каталог 1', NULL),
(111, 'Каталог 2', NULL);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "containers" ("id", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES
(100, 'eab0053353806d62467da0b5719254c3806887ec4ca61ccb65cda790f48b0252', 131, 4, 32, 35),
(101, '36e9453c25c39342e1851b0303cd506ebc464b4965b961b3cfb3af2c63f63020', 132, 4, 32, 36),
(102, '85dd0a6b0c3441156777b1015108496a86706a22f203c0f3a39214c109f957e8', 133, 4, 32, 37),
(103, 'c7d2dcd005889157fc58a889722be6e4d8f4c84b1266268ab0b6e76dd8ec3476', 134, 4, 32, 38),
(104, '80419563519d28c4c5382b323e8a7d5c31f78d9846b79a089142a3d4a9331990', 135, 4, 32, 39),
(105, '0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990', 136, 4, 32, 40);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(30, 110, (SELECT "id" FROM "empty_container"), '$ROOT$', 'inode/directory', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "folder_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(40, 110, 30, (SELECT "id" FROM "empty_container"), 'Директория 1', 'inode/directory', '2024-07-12T17:23:11Z'),
(41, 110, 30, (SELECT "id" FROM "empty_container"), 'Директория 2', 'inode/directory', '2024-07-12T17:23:11Z'),
(42, 110, 30, 100, 'Файл 1', 'application/pdf', '2024-07-12T17:23:11Z'),
(43, 110, 30, 101, 'Файл 2', 'application/xml', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "folder_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(44, 110, 41, (SELECT "id" FROM "empty_container"), 'Директория 3', 'inode/directory', '2024-07-12T17:23:11Z'),
(45, 110, 41, (SELECT "id" FROM "empty_container"), 'Директория 4', 'inode/directory', '2024-07-12T17:23:11Z'),
(46, 110, 41, 102, 'Файл 3', 'application/pdf', '2024-07-12T17:23:11Z'),
(47, 110, 41, 103, 'Файл 4', 'application/zip', '2024-07-12T17:23:11Z'),
(48, 110, 41, 104, 'Файл 5', 'application/xml', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
ALTER SEQUENCE "files_id_seq" RESTART WITH 147;

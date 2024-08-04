-- ------------------------------------------------------------------ --
-- Данные для тестирования API управления файловой системой картотеки --
-- ------------------------------------------------------------------ --
INSERT INTO "catalogs" ("id", "name", "description") OVERRIDING SYSTEM VALUE
VALUES
(112, 'Каталог 1', NULL),
(114, 'Каталог 2', NULL);

INSERT INTO "user_authorities" ("catalog_id", "user_uuid", "authority")
VALUES
(112, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41275', 'MASTER'),
(114, 'bfe5e92a-ba1f-4412-a5e9-2aba1fc41275', 'MASTER');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "containers" ("id", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES
(110, 'cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e', 131, 4, 32, 35),
(111, '36e9453c25c39342e1851b0303cd506ebc464b4965b961b3cfb3af2c63f63020', 132, 4, 32, 36),
(112, '85dd0a6b0c3441156777b1015108496a86706a22f203c0f3a39214c109f957e8', 133, 4, 32, 37),
(113, 'c7d2dcd005889157fc58a889722be6e4d8f4c84b1266268ab0b6e76dd8ec3476', 134, 4, 32, 38),
(114, '80419563519d28c4c5382b323e8a7d5c31f78d9846b79a089142a3d4a9331990', 135, 4, 32, 39),
(115, '0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990', 136, 4, 32, 40);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(465, 'TgPpdFgRfMz3Y3HBiEABkDxbrCacEt/SiCw2/MRgnTg='),
(466, 'G5Ko57hBafL155CEF+i49xPRcNmwjl3uNYUyxK4JJI0='),
(467, 'hEUCTv8+SFLS4IlGe4VR0EpEYMWGCdkvy+Ou4EunYx8='),
(468, 'Qd24YcF9ruS7LKbjF6MhtQujQOc7KhShOqfTXJni9VUQq5w=');

INSERT INTO "container_chunks" ("container_id", "chunk_id", "number")
VALUES
(110, 465, 1),
(110, 466, 2),
(110, 467, 3),
(110, 468, 4);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(31, 112, (SELECT "id" FROM "empty_container"), '$ROOT$', 'inode/directory', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "folder_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(50, 112, 31, (SELECT "id" FROM "empty_container"), 'Директория 1', 'inode/directory', '2024-07-12T17:23:11Z'),
(51, 112, 31, (SELECT "id" FROM "empty_container"), 'Директория 2', 'inode/directory', '2024-07-12T17:23:11Z'),
(52, 112, 31, 110, 'Файл 1', 'application/pdf', '2024-07-12T17:23:11Z'),
(53, 112, 31, 111, 'Файл 2', 'application/xml', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "folder_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(54, 112, 51, (SELECT "id" FROM "empty_container"), 'Директория 3', 'inode/directory', '2024-07-12T17:23:11Z'),
(55, 112, 51, (SELECT "id" FROM "empty_container"), 'Директория 4', 'inode/directory', '2024-07-12T17:23:11Z'),
(56, 112, 51, 112, 'Файл 3', 'application/pdf', '2024-07-12T17:23:11Z'),
(57, 112, 51, 113, 'Файл 4', 'application/zip', '2024-07-12T17:23:11Z'),
(58, 112, 51, 114, 'Файл 5', 'application/xml', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
ALTER SEQUENCE "files_id_seq" RESTART WITH 157;

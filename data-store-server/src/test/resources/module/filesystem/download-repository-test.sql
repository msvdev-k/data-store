-- ------------------------------------------ --
-- Данные для тестирования DownloadRepository --
-- ------------------------------------------ --
INSERT INTO "catalogs" ("id", "name", "description") OVERRIDING SYSTEM VALUE
VALUES
(115, 'Каталог 1', NULL);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(33, 115, (SELECT "id" FROM "empty_container"), '$ROOT$', 'inode/directory', '2024-07-12T17:23:11Z');




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "containers" ("id", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (108, 'f20aacfc293a9b8c8d0345960ba7b36035122d726abda130c25d5116eebe7437', 204, 6, 32, 44);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(360, '87+9PGogZ92e6N1UIDXIlVWEZaMEVVfFUwGGnXPFD30='),
(361, 'h9XlAs/r+amjFSJqj8LrwGwwuGWn8Lexar9w5jMh9NY='),
(362, 'WebxGrW8K6kUHX8Peno2MdQL4kTdt5O19mdDt3TsVQY='),
(363, 'nw/ZEno9K/D7DOB08u9v1fQ9JHlNmOCugytt96E3GGA='),
(364, 'EbQn6rFJ2pqHnqAWWexKX/aHJXkUEK0NAPSKl7faVdY='),
(365, 'bCjpAqJgR1aJ9iFe6GWuvUk5etZCSK5FQq23BMdTswjyb2rAxefytPj1Bgs=');

INSERT INTO "container_chunks" ("container_id", "chunk_id", "number")
VALUES
(108, 360, 1),
(108, 361, 2),
(108, 362, 3),
(108, 363, 4),
(108, 364, 5),
(108, 365, 6);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "containers" ("id", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (109, '4db703781440523ad9321ee163b1b4ad7d910b3b68f8c7c6348a4c224668b4ef', 107, 3, 32, 43);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(366, 'UfY3UhptwqSGQHc4zQKhJqqaFztSmzguV8qgfBhLwGc='),
(367, 'TteiKVEIPeAK2j8GOltSKTWkH/PUxenwFD2wVAZW9Ug='),
(368, 'a1yRgGzg3sZdWPKbdH8/X1rvP2BvccXIIgWLBrghtUZPYqquL1ydShvYiQ==');

INSERT INTO "container_chunks" ("container_id", "chunk_id", "number")
VALUES
(109, 366, 1),
(109, 367, 2),
(109, 368, 3);




-- -------------------------------- --
-- -------------------------------- --
INSERT INTO "files" ("id", "catalog_id", "folder_id", "container_id", "name", "mime_type", "create_date") OVERRIDING SYSTEM VALUE
VALUES
(60, 115, 33, (SELECT "id" FROM "empty_container"), 'Директория 1', 'inode/directory', '2024-07-12T17:23:11Z'),
(61, 115, 33, 108, 'Файл 1', 'application/pdf', '2024-07-12T17:23:11Z'),
(62, 115, 60, 109, 'Файл 2', 'application/pdf', '2024-07-12T17:23:11Z');

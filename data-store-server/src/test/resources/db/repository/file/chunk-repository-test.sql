-- --------------------------- --
-- Добавление фрагментов фйлов --
-- --------------------------- --
INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(1, 'aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI='),
(2, 'aXVuZmpHc2RoBmg4NzNneTRyYjNmYyA=');

ALTER SEQUENCE "chunks_id_seq" RESTART WITH 37;

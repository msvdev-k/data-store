-- ------------------------------- --
-- Добавление контейнера с данными --
-- ------------------------------- --
INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(21, '4UH4+gyc0GV6IoLnxN96hcQH2kmmWg/vxysxXKu4VFE='),
(22, 'wHj7vIJkq61M79rZqLnJy3DaRe2mOM3cMZI2ghq1H/I='),
(23, 'rWG4uz5U7yjPs+r2+kPjVw+CqdbTpc3f3jLyLy0OHu4='),
(24, 'szFP1rx//PPSoKW7GcQ43T4loXTUvdfVgQde0nudCooiSFRkbBI=');


INSERT INTO "containers" ("id", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (20, 'eab0053353806d62467da0b5719254c3806887ecbca61ccb65cda790f48b0252', 134, 4, 32, 38);

ALTER SEQUENCE "containers_id_seq" RESTART WITH 37;


INSERT INTO "container_chunks" ("container_id", "chunk_id", "number")
VALUES
(20, 21, 1),
(20, 22, 2),
(20, 23, 3),
(20, 24, 4);


-- ----------------------------------------------- --
-- Добавление сессии выгрузки контейнера с данными --
-- ----------------------------------------------- --
INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(51, 'jDtfw0ackTV7voJilcywvcV0BPPt4DMOBIwcTN1QBuE='),
(52, 'YpXriML4NngZOtAVKIRUAmYWdxyw7yj4IBYlkYriQM8='),
(53, 'VkZxervyWWOdAajCxYyTPfs4j5N1frYRj3F9jNrdBH8='),
(54, 'ZW/MO7aAcQwagAmR40XW/TN8ip+LYcLAZfTU4iB+nQQ='),
(55, 'EihQoG4nxlnFZOAHTmqGioPat463KtKpGtcEpj87wfw='),
(56, '4ErVGVIMeh15gxPep6YXO6C9Vjqy43EoC51c0jNI/eE='),
(57, '2VEmSQXYkME0RV8d338cRQYjlAqpmA==');


INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (5, 'PROCESSING', '1350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c6', 214, 7, 32, 22);


INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(5, 51, 1, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.922678500'),
(5, 52, 2, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.922678500'),
(5, 53, 3, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.923678500'),
(5, 54, 4, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.923678500'),
(5, 55, 5, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.923678500'),
(5, 56, 6, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.924678500'),
(5, 57, 7, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27.924678500');

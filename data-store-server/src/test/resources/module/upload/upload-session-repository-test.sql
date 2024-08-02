-- ----------------------------------------------- --
-- Данные для тестирования UploadSessionRepository --
-- ----------------------------------------------- --
INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES
(5, 'PROCESSING', '1350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c6', 214, 7, 32, 22),
(6, 'UPLOAD',     '2350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c0', 214, 7, 32, 22),
(7, 'UPLOAD',     '69f37482bd3ad6f0d1510bef1032e4efa3105b7d3411cbcb0d0b029d7e1df549', 114, 4, 32, 18);

ALTER SEQUENCE "upload_sessions_id_seq" RESTART WITH 37;




-- ----------------------------------------------- --
-- ----------------------------------------------- --
INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(51, 'jDtfw0ackTV7voJilcywvcV0BPPt4DMOBIwcTN1QBuE='),
(52, 'YpXriML4NngZOtAVKIRUAmYWdxyw7yj4IBYlkYriQM8='),
(53, 'VkZxervyWWOdAajCxYyTPfs4j5N1frYRj3F9jNrdBH8='),
(54, 'ZW/MO7aAcQwagAmR40XW/TN8ip+LYcLAZfTU4iB+nQQ='),
(55, 'EihQoG4nxlnFZOAHTmqGioPat463KtKpGtcEpj87wfw='),
(56, '4ErVGVIMeh15gxPep6YXO6C9Vjqy43EoC51c0jNI/eE='),
(57, '2VEmSQXYkME0RV8d338cRQYjlAqpmA=='),

(61, 'WtHrRi8IdXN50S+b5u+0MKGQ2Mv2vvvivEtLK4aU9qc='),
(62, 'mCtWVyDY0qV2YjggeSZDXwP9ORfh96/tZWOWj/uDeEA='),
(63, ''),
(64, '');




-- ----------------------------------------------- --
-- ----------------------------------------------- --
INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(5, 51, 1, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 52, 2, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 53, 3, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 54, 4, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 55, 5, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 56, 6, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),
(5, 57, 7, 'fea55e01-e825-4c17-9518-5c8418926029', 'PROCESSING', '2024-07-01T20:22:27Z'),

(7, 61, 1, '596aeb50-27a7-4361-ab12-e28ac6a4760c', 'PROCESSING', '2024-07-04T17:06:20Z'),
(7, 62, 2, '596aeb50-27a7-4361-ab12-e28ac6a4760c', 'PROCESSING', '2024-07-04T17:06:20Z'),
(7, 63, 3, '596aeb50-27a7-4361-ab12-e28ac6a4760c', 'UPLOAD',     '2024-07-04T17:06:20Z');

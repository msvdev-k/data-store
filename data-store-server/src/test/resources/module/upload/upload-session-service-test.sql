-- ---------------------------------------------------- --
-- Данные для тестирования сервиса UploadSessionService --
-- ---------------------------------------------------- --
INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (117, 'PROCESSING', '0c6c5f1cb1f143780f27ee0e79d977f527fb1b47b6b58f8111efc3e6510916cf', 184, 6, 32, 24);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(124, 'Gy61V4jPrunYtjon9yk36WEhYZ6CDBvW89LR/jBRC8Y='),
(125, 'mqZZtGHitEcOYnQ/D05W2gY47BmYy4U9LouZFxNsL8c='),
(126, 'Jn1DY3lAJpExh0ykK6Z2w3/2z+6i8VD8NLxQEkUoACE='),
(127, 'nSGpe7e1ShI0q+FoJNb9WlpZDj9RHu/n95PRTXbreew='),
(128, 'X/beTDXovO0s85ORJQlT3+tzVQ7Iujx90T7vyu1DWyo='),
(129, '05zJBMLqzzHUhON/LghBxckE+l8iNJyr');

INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(117, 124, 1, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33'),
(117, 125, 2, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33'),
(117, 126, 3, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33'),
(117, 127, 4, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33'),
(117, 128, 5, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33'),
(117, 129, 6, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T15:17:33');




-- ----------------------------------------------- --
-- ----------------------------------------------- --
INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (127, 'PROCESSING', 'c55323d4b347ae601b5c507856c7a520a932abe2070c40fa35aeeb8e62cfdb1c', 114, 4, 32, 18);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(134, 'POKN2SblpvK8qAshWaMJgoiCtfYiKhF9IqnQ/pAUJyA='),
(135, ''),
(136, ''),
(137, '');

INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(127, 134, 1, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:10:06'),
(127, 135, 2, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d1', 'UPLOAD',     '2024-08-02T13:10:06'),
(127, 136, 3, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d2', 'UPLOAD',     '2024-08-02T13:10:06'),
(127, 137, 4, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d3', 'UPLOAD',     '2024-08-02T13:10:06');




-- ----------------------------------------------- --
-- ----------------------------------------------- --
INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (137, 'PROCESSING', '0c535d44777d76e3d463d268293c5a9763b1c1336cb52418642dc5d368dfaf60', 204, 6, 32, 44);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(144, 'ZQaG9xeeW0yshbVnJXs3zCRn/f4XUBGAbKMAmCG64D4='),
(145, 'rm2HbpFfpCOgzdFNIEUEvT7vWBj+uOe8bDVyirBdrVQ='),
(146, '3ASIPQNCAMbh/k1xmih6ViaQ1UnleCoMW11LxnycZEg='),
(147, 'UsgDAjwX9qQqkkwClUUTY3ITms7mq4z7c7GA7eDyURE='),
(148, '+q82VD5gc8dYUQGrY6C/On/ykl2OXIdoVod52MgywBU='),
(149, 'wRtnssF/99vv13SpLNiV1usKp41cXvXqiPy6bjYib+sYUGE9r7wCMZOjB6E=');

INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(137, 144, 1, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25'),
(137, 145, 2, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25'),
(137, 146, 3, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25'),
(137, 147, 4, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25'),
(137, 148, 5, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25'),
(137, 149, 6, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T13:11:25');




-- ----------------------------------------------- --
-- -error Hash------------------------------------ --
INSERT INTO "upload_sessions" ("id", "state", "sha256", "size", "chunk_count", "chunk_size", "last_chunk_size") OVERRIDING SYSTEM VALUE
VALUES (147, 'PROCESSING', '1cd0b036f07338bfa53cfad34e2d3f904a5c481cf6c26e5991ecebecc04416eA', 132, 4, 32, 36);

INSERT INTO "chunks" ("id", "content") OVERRIDING SYSTEM VALUE
VALUES
(154, 'PF2rXODu4sEyZG0swe1LDcHt19W3lwFy5BCJjeefCMk='),
(155, 'awI7qh4loDMO9nsBxXV1Ko5IgLqrAvOVgvj2Ty3ZaQY='),
(156, 'wk9D54FEBq2QdX1vMO9+CeoBgHIGWSS2rKS2sviCn1w='),
(157, 'chdEsdmhiVVO70AC1Ukr49cLTUoDKAQLGWOIOJXUAXFrVvJe');

INSERT INTO "upload_chunks" ("upload_session_id", "chunk_id", "number", "user_uuid", "state", "last_modified")
VALUES
(147, 154, 1, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T16:24:16'),
(147, 155, 2, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T16:24:16'),
(147, 156, 3, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T16:24:16'),
(147, 157, 4, 'cf13dd5d-0092-47c7-93dd-5d0092f7c7d0', 'PROCESSING', '2024-08-02T16:24:16');




-- ----------------------------------------------- --
-- ----------------------------------------------- --
ALTER SEQUENCE "upload_sessions_id_seq" RESTART WITH 237;



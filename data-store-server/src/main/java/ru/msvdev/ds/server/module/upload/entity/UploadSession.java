package ru.msvdev.ds.server.module.upload.entity;

import ru.msvdev.ds.server.module.upload.base.UploadSessionState;

/**
 * Сессия выгрузки содержимого файла на сервер
 *
 * @param id            идентификатор сессии
 * @param state         состояние сессии {@link UploadSessionState}
 * @param sha256        hash-сумма содержимого файла hex(sha256(content))
 * @param size          размер файла (байт)
 * @param chunkCount    количество фрагментов, на которое разбит файл
 * @param chunkSize     размер фрагментов (байт)
 * @param lastChunkSize размер последнего фрагмента (байт)
 */
public record UploadSession(
        long id,
        UploadSessionState state,
        String sha256,
        long size,
        int chunkCount,
        int chunkSize,
        int lastChunkSize
) {
}

package ru.msvdev.ds.server.dao.entity.file;

import ru.msvdev.ds.server.utils.file.UploadSessionState;

/**
 * Сессия выгрузки бинарных данных на сервер
 *
 * @param id            идентификатор сессии
 * @param state         состояние сессии {@link UploadSessionState}
 * @param sha256        hash-сумма контейнера данных hex(sha256(content))
 * @param size          размер контейнера (байт)
 * @param chunkCount    количество фрагментов, на которое разбит контейнер
 * @param chunkSize     размер фрагментов контейнера (байт)
 * @param lastChunkSize размер последнего фрагмента контейнера (байт)
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

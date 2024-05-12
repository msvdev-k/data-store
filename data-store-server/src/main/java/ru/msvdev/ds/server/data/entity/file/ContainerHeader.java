package ru.msvdev.ds.server.data.entity.file;

/**
 * Заголовок контейнера содержащего бинарные данные
 *
 * @param id            идентификатор контейнера
 * @param sha256        hash-сумма контейнера данных hex(sha256(content))
 * @param size          размер контейнера данных (байт)
 * @param chunkCount    количество фрагментов, на которое разбит контейнер
 * @param chunkSize     размер фрагментов контейнера (байт)
 * @param lastChunkSize размер последнего фрагмента контейнера (байт)
 */
public record ContainerHeader(
        long id,
        String sha256,
        long size,
        int chunkCount,
        int chunkSize,
        int lastChunkSize
) {
}

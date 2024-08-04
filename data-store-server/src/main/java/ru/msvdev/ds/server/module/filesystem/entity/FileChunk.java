package ru.msvdev.ds.server.module.filesystem.entity;

/**
 * Описание содержимого фрагмента файла
 *
 * @param sha256        hash-сумма всего файла (SHA-256)
 * @param size          размер файла (байт)
 * @param chunkCount    количество фрагментов на которое разбит файл
 * @param chunkSize     размер фрагментов (байт)
 * @param lastChunkSize размер последнего фрагмента (байт)
 * @param number        порядковый текущего номер фрагмента
 * @param content       содержимое текущего фрагмента (строка в формате Base64)
 */
public record FileChunk(
        String sha256,
        long size,
        int chunkCount,
        int chunkSize,
        int lastChunkSize,
        int number,
        String content
) {
}

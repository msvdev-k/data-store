package ru.msvdev.ds.server.module.filesystem.entity;

import java.time.OffsetDateTime;

/**
 * Дескриптор файла (каталога) файловой системы картотеки
 *
 * @param id         идентификатор файла
 * @param name       название файла
 * @param mimeType   тип данных файла
 * @param createDate дата и время создания файла
 * @param size       размер файла (байт)
 */
public record FileInfo(
        long id,
        String name,
        String mimeType,
        OffsetDateTime createDate,
        long size
) {
    public static final String DIRECTORY_MIME_TYPE = "inode/directory";

    public boolean isDirectory() {
        return mimeType.equals(DIRECTORY_MIME_TYPE);
    }

    public boolean isFile() {
        return !mimeType.equals(DIRECTORY_MIME_TYPE);
    }
}

package ru.msvdev.ds.server.data.entity.file;

import java.time.OffsetDateTime;

/**
 * Дескриптор файла (каталога) файловой системы картотеки
 *
 * @param id         идентификатор файла
 * @param folderId   идентификатор каталога, которому принадлежит файл
 * @param name       название файла
 * @param mimeType   тип данных файла
 * @param createDate дата и время создания файла
 * @param size       размер файла (байт)
 */
public record FileInfo(
        long id,
        long folderId,
        String name,
        String mimeType,
        OffsetDateTime createDate,
        long size
) {
    public boolean isRoot() {
        return folderId < 0;
    }

    public boolean isDirectory() {
        return mimeType.equals("inode/directory");
    }

    public boolean isFile() {
        return !mimeType.equals("inode/directory");
    }
}

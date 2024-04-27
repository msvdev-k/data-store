package ru.msvdev.ds.server.data.entity.file;

import java.time.OffsetDateTime;

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

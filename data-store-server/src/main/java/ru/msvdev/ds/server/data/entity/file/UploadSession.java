package ru.msvdev.ds.server.data.entity.file;

import ru.msvdev.ds.server.utils.file.UploadFileState;

public record UploadSession(
        long id,
        UploadFileState state,
        String sha256,
        long size,
        int chunkCount,
        int chunkSize,
        int lastChunkSize
) {
}

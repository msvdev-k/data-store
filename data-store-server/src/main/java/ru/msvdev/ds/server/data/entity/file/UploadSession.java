package ru.msvdev.ds.server.data.entity.file;

import ru.msvdev.ds.server.utils.file.UploadSessionState;

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

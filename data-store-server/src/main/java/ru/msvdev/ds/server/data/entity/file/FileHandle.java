package ru.msvdev.ds.server.data.entity.file;

public record FileHandle(
        long id,
        String sha256,
        String mimeType,
        long size,
        int chunkCount,
        int chunkSize,
        int lastChunkSize
) {
}

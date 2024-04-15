package ru.msvdev.ds.server.data.entity.file;

public record FileChunk(
        Integer size,
        String content,
        Integer number
) {
}

package ru.msvdev.ds.server.module.filesystem.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.filesystem.entity.FileChunk;
import ru.msvdev.ds.server.module.upload.base.ChunkingSchema;
import ru.msvdev.ds.server.openapi.model.DownloadResponse;


@Component
@RequiredArgsConstructor
public class DownloadResponseMapper {

    public DownloadResponse getResponse(long nodeId, FileChunk fileChunk) {
        ChunkingSchema schema = new ChunkingSchema(
                fileChunk.size(), fileChunk.chunkCount(), fileChunk.chunkSize(), fileChunk.lastChunkSize()
        );

        DownloadResponse response = new DownloadResponse();

        response.setNodeId(nodeId);
        response.setSha256(fileChunk.sha256());
        response.setSize(fileChunk.size());
        response.setChunkCount(fileChunk.chunkCount());
        response.setChunkSize(fileChunk.chunkSize());
        response.setLastChunkSize(fileChunk.lastChunkSize());
        response.setNumber(fileChunk.number());
        response.setOffset(schema.getOffsetChunk(fileChunk.number()));
        response.setContentSize(schema.getChunkSize(fileChunk.number()));
        response.setContent(fileChunk.content());

        return response;
    }

}

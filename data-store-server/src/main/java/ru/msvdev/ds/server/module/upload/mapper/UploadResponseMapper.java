package ru.msvdev.ds.server.module.upload.mapper;

import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.upload.base.ChunkingSchema;
import ru.msvdev.ds.server.module.upload.entity.ContainerHeader;
import ru.msvdev.ds.server.module.upload.entity.UploadSession;
import ru.msvdev.ds.server.openapi.model.UploadResponse;
import ru.msvdev.ds.server.openapi.model.UploadState;

import java.time.OffsetDateTime;


@Component
public class UploadResponseMapper {

    public UploadResponse getResponse(ContainerHeader containerHeader) {
        UploadResponse response = new UploadResponse();
        response.setState(UploadState.ARCHIVE);

        response.setSha256(containerHeader.sha256());
        response.setSize(containerHeader.size());

        response.setChunkCount(containerHeader.chunkCount());
        response.setChunkSize(containerHeader.chunkSize());
        response.setLastChunkSize(containerHeader.lastChunkSize());

        response.setUploadSession(null);

        response.setChunkNumber(null);
        response.setUploadOffset(null);
        response.setUploadSize(null);
        response.setUploadEnd(null);

        return response;
    }


    public UploadResponse getResponse(UploadSession uploadSession) {
        UploadResponse response = new UploadResponse();
        response.setState(UploadState.valueOf(uploadSession.state().name()));

        response.setSha256(uploadSession.sha256());
        response.setSize(uploadSession.size());

        response.setChunkCount(uploadSession.chunkCount());
        response.setChunkSize(uploadSession.chunkSize());
        response.setLastChunkSize(uploadSession.lastChunkSize());

        response.setUploadSession(uploadSession.id());

        response.setChunkNumber(null);
        response.setUploadOffset(null);
        response.setUploadSize(null);
        response.setUploadEnd(null);

        return response;
    }


    public UploadResponse getResponse(Long sessionId, UploadState state, String sha256, ChunkingSchema schema, int chunkNumber, OffsetDateTime uploadEnd) {
        UploadResponse response = new UploadResponse();
        response.setState(state);

        response.setSha256(sha256);
        response.setSize(schema.size());

        response.setChunkCount(schema.count());
        response.setChunkSize(schema.chunkSize());
        response.setLastChunkSize(schema.lastChunkSize());

        response.setUploadSession(sessionId);

        if (chunkNumber > 0) {
            response.setChunkNumber(chunkNumber);
            response.setUploadOffset(schema.getOffsetChunk(chunkNumber));
            response.setUploadSize(schema.getChunkSize(chunkNumber));
            response.setUploadEnd(uploadEnd);
        } else {
            response.setChunkNumber(null);
            response.setUploadOffset(null);
            response.setUploadSize(null);
            response.setUploadEnd(null);
        }

        return response;
    }
}

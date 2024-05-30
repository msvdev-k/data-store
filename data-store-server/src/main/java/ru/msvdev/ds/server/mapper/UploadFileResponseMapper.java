package ru.msvdev.ds.server.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.dao.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.dao.entity.file.ContainerHeader;
import ru.msvdev.ds.server.dao.entity.file.UploadSession;
import ru.msvdev.ds.server.openapi.model.UploadFileResponse;
import ru.msvdev.ds.server.openapi.model.UploadFileState;
import ru.msvdev.ds.server.property.UploadSessionProperty;

import java.time.OffsetDateTime;


@Component
@RequiredArgsConstructor
public class UploadFileResponseMapper {

    private final UploadSessionProperty property;


    public UploadFileResponse getResponse(ContainerHeader containerHeader) {
        UploadFileResponse response = new UploadFileResponse();
        response.setState(UploadFileState.ARCHIVE);

        response.setSha256(containerHeader.sha256());
        response.setSize(containerHeader.size());

        response.setChunkCount(containerHeader.chunkCount());
        response.setChunkSize(containerHeader.chunkSize());
        response.setLastChunkSize(containerHeader.lastChunkSize());

        response.setUploadSession(null);

        response.setUploadNumber(null);
        response.setUploadOffset(null);
        response.setUploadSize(null);
        response.setUploadEnd(null);

        return response;
    }


    public UploadFileResponse getResponse(UploadSession uploadSession) {
        UploadFileResponse response = new UploadFileResponse();
        response.setState(UploadFileState.valueOf(uploadSession.state().name()));

        response.setSha256(uploadSession.sha256());
        response.setSize(uploadSession.size());

        response.setChunkCount(uploadSession.chunkCount());
        response.setChunkSize(uploadSession.chunkSize());
        response.setLastChunkSize(uploadSession.lastChunkSize());

        response.setUploadSession(uploadSession.id());

        response.setUploadNumber(null);
        response.setUploadOffset(null);
        response.setUploadSize(null);
        response.setUploadEnd(null);

        return response;
    }


    public UploadFileResponse getResponse(Long sessionId, UploadFileState state, String sha256, ChunkingSchema schema, int chunkNumber) {
        UploadFileResponse response = new UploadFileResponse();
        response.setState(state);

        response.setSha256(sha256);
        response.setSize(schema.size());

        response.setChunkCount(schema.count());
        response.setChunkSize(schema.chunkSize());
        response.setLastChunkSize(schema.lastChunkSize());

        response.setUploadSession(sessionId);

        if (chunkNumber > 0) {
            response.setUploadNumber(chunkNumber);
            response.setUploadOffset(schema.getOffsetChunk(chunkNumber));
            response.setUploadSize(schema.getChunkSize(chunkNumber));
            response.setUploadEnd(OffsetDateTime.now().plus(property.uploadChunkTimeout()));
        } else {
            response.setUploadNumber(null);
            response.setUploadOffset(null);
            response.setUploadSize(null);
            response.setUploadEnd(null);
        }

        return response;
    }
}

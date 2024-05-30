package ru.msvdev.ds.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.service.file.UploadSessionService;
import ru.msvdev.ds.server.openapi.api.FileApi;
import ru.msvdev.ds.server.openapi.model.UploadChunkRequest;
import ru.msvdev.ds.server.openapi.model.UploadFileRequest;
import ru.msvdev.ds.server.openapi.model.UploadFileResponse;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class FileController implements FileApi {

    private final UploadSessionService uploadSessionService;

    @Override
    public ResponseEntity<UploadFileResponse> addChunk(UUID userUUID, Long catalogId, UploadChunkRequest uploadChunkRequest) {
        UploadFileResponse uploadFileResponse = uploadSessionService.addChunk(userUUID, uploadChunkRequest);
        return ResponseEntity.ok(uploadFileResponse);
    }

    @Override
    public ResponseEntity<UploadFileResponse> openUploadSession(UUID userUUID, Long catalogId, UploadFileRequest uploadFileRequest, Boolean resetError) {
        UploadFileResponse uploadFileResponse = uploadSessionService.openUploadSession(userUUID, uploadFileRequest, resetError);
        return ResponseEntity.ok(uploadFileResponse);
    }
}

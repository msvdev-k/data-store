package ru.msvdev.ds.server.module.upload.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.msvdev.ds.server.module.upload.service.UploadSessionService;
import ru.msvdev.ds.server.openapi.api.UploadApi;
import ru.msvdev.ds.server.openapi.model.UploadChunkRequest;
import ru.msvdev.ds.server.openapi.model.UploadRequest;
import ru.msvdev.ds.server.openapi.model.UploadResponse;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class UploadController implements UploadApi {

    private final UploadSessionService uploadSessionService;

    @Override
    public ResponseEntity<UploadResponse> addChunk(UUID userUUID, Long catalogId, UploadChunkRequest uploadChunkRequest) {
        UploadResponse uploadFileResponse = uploadSessionService.addChunk(userUUID, uploadChunkRequest);
        return ResponseEntity.ok(uploadFileResponse);
    }

    @Override
    public ResponseEntity<UploadResponse> openSession(UUID userUUID, Long catalogId, UploadRequest uploadFileRequest, Boolean resetError) {
        UploadResponse uploadFileResponse = uploadSessionService.openSession(userUUID, uploadFileRequest, resetError);
        return ResponseEntity.ok(uploadFileResponse);
    }
}

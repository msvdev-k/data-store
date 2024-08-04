package ru.msvdev.ds.server.module.filesystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import ru.msvdev.ds.server.openapi.api.FsApi;
import ru.msvdev.ds.server.openapi.model.*;
import ru.msvdev.ds.server.module.filesystem.service.FileSystemService;

import java.util.List;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class FileSystemController implements FsApi {

    private final FileSystemService fileSystemService;


    @Override
    public ResponseEntity<List<FileSystemResponse>> getFiles(UUID userUUID, Long catalogId, Long nodeId) {
        List<FileSystemResponse> responses = fileSystemService.findAll(catalogId, nodeId);
        return ResponseEntity.ok(responses);
    }


    @Override
    public ResponseEntity<FileSystemResponse> newFile(UUID userUUID, Long catalogId, Long nodeId, FileSystemRequest fileSystemRequest) {
        FileSystemResponse response = fileSystemService.addNode(catalogId, nodeId, fileSystemRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @Override
    public ResponseEntity<Void> removeFile(UUID userUUID, Long catalogId, Long nodeId) {
        fileSystemService.remove(catalogId, nodeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Override
    public ResponseEntity<FileSystemResponse> renameFile(UUID userUUID, Long catalogId, Long nodeId, RenameFileRequest renameFileRequest) {
        FileSystemResponse response = fileSystemService.rename(catalogId, nodeId, renameFileRequest);
        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<DownloadResponse> getFileContent(UUID userUUID, Long catalogId, Long nodeId, Integer chunk) {
        DownloadResponse response = fileSystemService.getFileContent(catalogId, nodeId, chunk);
        return ResponseEntity.ok(response);
    }

}

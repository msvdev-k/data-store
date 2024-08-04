package ru.msvdev.ds.server.module.filesystem.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.msvdev.ds.server.module.filesystem.entity.FileInfo;
import ru.msvdev.ds.server.openapi.model.FileSystemResponse;


@Component
@RequiredArgsConstructor
public class FileSystemResponseMapper {

    public FileSystemResponse getResponse(FileInfo fileInfo) {
        FileSystemResponse response = new FileSystemResponse();

        response.setNodeId(fileInfo.id());
        response.setName(fileInfo.name());
        response.setMimeType(fileInfo.mimeType());
        response.setSize(fileInfo.size());
        response.setCreated(fileInfo.createDate());

        return response;
    }

}

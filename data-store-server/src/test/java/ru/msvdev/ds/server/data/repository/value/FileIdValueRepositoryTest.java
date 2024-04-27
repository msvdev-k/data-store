package ru.msvdev.ds.server.data.repository.value;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.msvdev.ds.server.data.entity.Catalog;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.data.repository.CatalogRepository;
import ru.msvdev.ds.server.data.repository.file.FileHandleRepository;
import ru.msvdev.ds.server.data.repository.file.FileRepository;

import java.time.OffsetDateTime;


class FileIdValueRepositoryTest extends ValueRepositoryTest {

    private final FileIdValueRepository fileIdValueRepository;
    private final FileRepository fileRepository;
    private final CatalogRepository catalogRepository;
    private final FileHandleRepository fileHandleRepository;


    @Autowired
    public FileIdValueRepositoryTest(FileIdValueRepository fileIdValueRepository, FileRepository fileRepository, CatalogRepository catalogRepository, FileHandleRepository fileHandleRepository) {
        this.fileIdValueRepository = fileIdValueRepository;
        this.fileRepository = fileRepository;
        this.catalogRepository = catalogRepository;
        this.fileHandleRepository = fileHandleRepository;
    }

    private long fileId;


    @BeforeEach
    void setUp() {
        Catalog catalog = catalogRepository.insert("Catalog 1", "");

        long size = 1024 * 1024 * 4 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;
        ChunkingSchema fileChunking = ChunkingSchema.of(size, chunkSize, minChunkSize);

        long fileHandleId = fileHandleRepository.insert(
                "f5d343132f7ab1938e186ce599d75fca793022331deb73e544bddbe95538c742",
                "application/json",
                fileChunking.size(), fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );

        fileRepository.insertRoot(catalog.id(), OffsetDateTime.now());
        fileId = fileRepository.insertToRoot(catalog.id(), fileHandleId, "File Name", OffsetDateTime.now());
    }


    @Test
    void baseTest() {
        baseFindInsertTest(fileIdValueRepository, fileId, Assertions::assertEquals);
    }

}
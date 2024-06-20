package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.file.Chunk;
import ru.msvdev.ds.server.dao.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.dao.entity.file.ContainerHeader;
import ru.msvdev.ds.server.utils.file.UploadSessionState;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InsertContainerFromUploadSessionTest extends ApplicationTest {

    private final UploadSessionRepository uploadSessionRepository;
    private final ChunkRepository chunkRepository;
    private final ContainerRepository containerRepository;

    @Autowired
    public InsertContainerFromUploadSessionTest(UploadSessionRepository uploadSessionRepository, ChunkRepository chunkRepository, ContainerRepository containerRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.chunkRepository = chunkRepository;
        this.containerRepository = containerRepository;
    }

    private String sha256;
    private ChunkingSchema fileChunking;
    private long uploadSessionId;


    @BeforeEach
    void setUp() {
        sha256 = "f5d343132f7ab1938e186ce599d75fca793022331deb73e544bddbe95538c742";

        long size = 1024 * 1024 * 10 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;
        fileChunking = ChunkingSchema.of(size, chunkSize, minChunkSize);

        UUID userUUID = UUID.randomUUID();

        long[] chunkIdArray = new long[fileChunking.count()];
        for (int i = 0; i < fileChunking.count() - 1; i++) {
            chunkIdArray[i] = chunkRepository.insert(fileChunking.chunkSize(), String.valueOf(i + 1));
        }
        chunkIdArray[fileChunking.count() - 1] = chunkRepository.insert(fileChunking.lastChunkSize(), String.valueOf(fileChunking.count()));

        uploadSessionId = uploadSessionRepository.insert(
                UploadSessionState.PROCESSING, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );

        for (int i = 0; i < fileChunking.count(); i++) {
            uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                    chunkIdArray[i], i + 1, UploadSessionState.PROCESSING, OffsetDateTime.now());
        }
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void insertFromUploadSessionTest() {
        long begin = System.nanoTime();
        boolean insertResult = containerRepository.insertFromUploadSession(uploadSessionId);
        long end = System.nanoTime();

        System.out.printf("%f мс для %d фрагментов%n", (end - begin) * 1e-6, fileChunking.count());

        assertTrue(insertResult);

        ContainerHeader containerHeader = containerRepository.findBySha256(sha256);

        assertTrue(containerHeader.id() > 0);
        assertEquals(sha256, containerHeader.sha256());
        assertEquals(fileChunking.size(), containerHeader.size());
        assertEquals(fileChunking.count(), containerHeader.chunkCount());
        assertEquals(fileChunking.chunkSize(), containerHeader.chunkSize());
        assertEquals(fileChunking.lastChunkSize(), containerHeader.lastChunkSize());

        for (int i = 0; i < fileChunking.count(); i++) {
            int chunkNumber = i + 1;
            Chunk chunk = containerRepository.findChunk(containerHeader.id(), chunkNumber);

            assertEquals(fileChunking.getChunkSize(chunkNumber), chunk.size());
            assertEquals(String.valueOf(chunkNumber), chunk.content());
            assertEquals(chunkNumber, chunk.number());
        }
    }

}

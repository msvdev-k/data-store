package ru.msvdev.ds.server.data.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.data.entity.file.UploadSession;
import ru.msvdev.ds.server.utils.file.UploadFileState;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UploadSessionRepositoryTest extends ApplicationTest {

    private final UploadSessionRepository uploadSessionRepository;

    @Autowired
    public UploadSessionRepositoryTest(UploadSessionRepository uploadSessionRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
    }

    private String sha256;
    private ChunkingSchema fileChunking;


    @BeforeEach
    void setUp() {
        sha256 = "f5d343132f7ab1938e186ce599d75fca793022331deb73e544bddbe95538c742";

        long size = 1024 * 1024 * 4 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;
        fileChunking = ChunkingSchema.of(size, chunkSize, minChunkSize);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void insertTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        assertThrowsExactly(DuplicateKeyException.class, () ->
                uploadSessionRepository.insert(UploadFileState.UPLOAD, sha256, 0, 0, 0, 0)
        );
    }


    @Test
    void updateStateTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        assertTrue(uploadSessionRepository.updateState(uploadSessionId, UploadFileState.UPLOAD));
        assertTrue(uploadSessionRepository.updateState(uploadSessionId, UploadFileState.PROCESSING));
        assertTrue(uploadSessionRepository.updateState(uploadSessionId, UploadFileState.ERROR));
    }


    @Test
    void deleteTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        assertTrue(uploadSessionRepository.delete(uploadSessionId));
        // Повторное удаление не происходит
        assertFalse(uploadSessionRepository.delete(uploadSessionId));
    }


    @Test
    void findByIdTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        UploadSession uploadSession = uploadSessionRepository.findById(uploadSessionId);

        assertNotNull(uploadSession);
        assertEquals(uploadSessionId, uploadSession.id());
        assertEquals(UploadFileState.UPLOAD, uploadSession.state());
        assertEquals(sha256, uploadSession.sha256());
        assertEquals(fileChunking.size(), uploadSession.size());
        assertEquals(fileChunking.count(), uploadSession.chunkCount());
        assertEquals(fileChunking.chunkSize(), uploadSession.chunkSize());
        assertEquals(fileChunking.lastChunkSize(), uploadSession.lastChunkSize());
    }


    @Test
    void findBySha256Test() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        UploadSession uploadSession = uploadSessionRepository.findBySha256(sha256);

        assertNotNull(uploadSession);
        assertEquals(uploadSessionId, uploadSession.id());
        assertEquals(UploadFileState.UPLOAD, uploadSession.state());
        assertEquals(sha256, uploadSession.sha256());
        assertEquals(fileChunking.size(), uploadSession.size());
        assertEquals(fileChunking.count(), uploadSession.chunkCount());
        assertEquals(fileChunking.chunkSize(), uploadSession.chunkSize());
        assertEquals(fileChunking.lastChunkSize(), uploadSession.lastChunkSize());
    }
}

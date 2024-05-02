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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UploadSessionRepositoryTest extends ApplicationTest {

    private final UploadSessionRepository uploadSessionRepository;
    private final ChunkRepository chunkRepository;

    @Autowired
    public UploadSessionRepositoryTest(UploadSessionRepository uploadSessionRepository, ChunkRepository chunkRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.chunkRepository = chunkRepository;
    }

    private String sha256;
    private ChunkingSchema fileChunking;
    private UUID userUUID;
    private long[] chunkIdArray;


    @BeforeEach
    void setUp() {
        sha256 = "f5d343132f7ab1938e186ce599d75fca793022331deb73e544bddbe95538c742";

        long size = 1024 * 1024 * 4 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;
        fileChunking = ChunkingSchema.of(size, chunkSize, minChunkSize);

        userUUID = UUID.randomUUID();

        chunkIdArray = new long[fileChunking.count()];
        for (int i = 0; i < fileChunking.count() - 1; i++) {
            chunkIdArray[i] = chunkRepository.insert(fileChunking.chunkSize(), "");
        }
        chunkIdArray[fileChunking.count() - 1] = chunkRepository.insert(fileChunking.lastChunkSize(), "");
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


    @Test
    void insertUploadChunkTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        for (int i = 1; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], i, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }

        assertThrowsExactly(DuplicateKeyException.class, () ->
                uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                        chunkIdArray[0], 1, UploadFileState.UPLOAD, OffsetDateTime.now())
        );
    }


    @Test
    void updateUploadChunkStateTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], i + 1, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }


        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.updateUploadChunkState(userUUID, uploadSessionId,
                            i + 1, UploadFileState.PROCESSING, OffsetDateTime.now())
            );
        }
    }


    @Test
    void deleteUploadChunkTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], i + 1, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }


        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(uploadSessionRepository.deleteUploadChunk(uploadSessionId, i + 1));
        }

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertFalse(uploadSessionRepository.deleteUploadChunk(uploadSessionId, i + 1));
        }
    }


    @Test
    void deleteUploadChunksTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], i + 1, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }

        assertTrue(uploadSessionRepository.deleteUploadChunk(uploadSessionId, UploadFileState.UPLOAD, OffsetDateTime.now()));

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertFalse(uploadSessionRepository.deleteUploadChunk(uploadSessionId, i + 1));
        }
    }


    @Test
    void findChunkNumbersTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < chunkIdArray.length; i++) {
            numbers.add(chunkIdArray.length - i);
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], chunkIdArray.length - i, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }


        int[] findNumbers = uploadSessionRepository.findChunkNumbers(uploadSessionId);
        System.out.println(Arrays.toString(findNumbers));

        assertEquals(numbers.size(), findNumbers.length);
        for (int findNumber : findNumbers) {
            assertTrue(numbers.contains(findNumber));
        }
    }


    @Test
    void findChunkIdTest() {
        Long uploadSessionId = uploadSessionRepository.insert(
                UploadFileState.UPLOAD, sha256, fileChunking.size(),
                fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        assertNotNull(uploadSessionId);

        for (int i = 0; i < chunkIdArray.length; i++) {
            assertTrue(
                    uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId,
                            chunkIdArray[i], i + 1, UploadFileState.UPLOAD, OffsetDateTime.now())
            );
        }


        for (int i = 0; i < chunkIdArray.length; i++) {
            Long chunkId = uploadSessionRepository.findChunkId(userUUID, uploadSessionId, i + 1);

            assertNotNull(chunkId);
            assertEquals(chunkIdArray[i], chunkId);
        }

        UUID user = UUID.randomUUID();
        for (int i = 0; i < chunkIdArray.length; i++) {
            Long chunkId = uploadSessionRepository.findChunkId(user, uploadSessionId, i + 1);

            assertNull(chunkId);
        }
    }

}

package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.utils.file.UploadSessionState;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/file/upload-session-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UploadSessionChunkRepositoryTest extends ApplicationTest {

    private final UploadSessionRepository uploadSessionRepository;
    private final ChunkRepository chunkRepository;

    @Autowired
    public UploadSessionChunkRepositoryTest(UploadSessionRepository uploadSessionRepository, ChunkRepository chunkRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
        this.chunkRepository = chunkRepository;
    }


    @Test
    void deleteUploadChunks1() {
        // region Given
        long uploadSessionId = 5;
        long[] chunkIds = new long[]{51, 52, 53, 54, 55, 56, 57};
        // endregion


        // region When
        boolean deleteFlag = uploadSessionRepository.deleteUploadChunks(uploadSessionId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        for (long chunkId : chunkIds) {
            assertNull(chunkRepository.findContent(chunkId));
        }
        // endregion
    }


    @Test
    void deleteUploadChunks2() {
        // region Given
        UploadSessionState state = UploadSessionState.PROCESSING;
        OffsetDateTime lastModified = OffsetDateTime.now();
        long[] chunkIds = new long[]{51, 52, 53, 54, 55, 56, 57, 61, 62, 63};
        // endregion


        // region When
        boolean deleteFlag = uploadSessionRepository.deleteUploadChunks(state, lastModified);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        for (long chunkId : chunkIds) {
            if (chunkId != 63) {
                assertNull(chunkRepository.findContent(chunkId));
            } else {
                assertNotNull(chunkRepository.findContent(chunkId));
            }
        }
        // endregion
    }

}

package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.file.UploadSession;
import ru.msvdev.ds.server.utils.file.UploadSessionState;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/file/upload-session-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UploadSessionRepositoryTest extends ApplicationTest {

    private final UploadSessionRepository uploadSessionRepository;

    @Autowired
    public UploadSessionRepositoryTest(UploadSessionRepository uploadSessionRepository) {
        this.uploadSessionRepository = uploadSessionRepository;
    }


    @Test
    void insert() {
        // region Given
        UploadSessionState state = UploadSessionState.UPLOAD;
        String sha256 = "5e42862836d92d48960e5e7329eac476ee002b39f2bd5cd884e11f64e0d96124";
        long size = 214;
        int chunkCount = 7;
        int chunkSize = 32;
        int lastChunkSize = 22;

        long expectedId = 37;
        // endregion


        // region When
        Long insertedId = uploadSessionRepository.insert(state, sha256, size, chunkCount, chunkSize, lastChunkSize);
        // endregion


        // region Then
        assertNotNull(insertedId);
        assertEquals(expectedId, insertedId);

        // endregion
    }


    @ParameterizedTest
    @EnumSource(UploadSessionState.class)
    void updateState(UploadSessionState state) {
        // region Given
        long id = 5;
        // endregion

        // region When
        boolean updateFlag = uploadSessionRepository.updateState(id, state);
        // endregion

        // region Then
        assertTrue(updateFlag);
        // endregion
    }


    @Test
    void delete() {
        // region Given
        long id = 5;
        // endregion


        // region When
        boolean deleteFlag = uploadSessionRepository.delete(id);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        // endregion
    }


    @Test
    void findById() {
        // region Given
        long id = 5;
        UploadSessionState state = UploadSessionState.PROCESSING;
        String sha256 = "1350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c6";
        long size = 214;
        int chunkCount = 7;
        int chunkSize = 32;
        int lastChunkSize = 22;
        // endregion


        // region When
        UploadSession foundUploadSession = uploadSessionRepository.findById(id);
        // endregion


        // region Then
        assertNotNull(foundUploadSession);
        assertEquals(id, foundUploadSession.id());
        assertEquals(state, foundUploadSession.state());
        assertEquals(sha256, foundUploadSession.sha256());
        assertEquals(size, foundUploadSession.size());
        assertEquals(chunkCount, foundUploadSession.chunkCount());
        assertEquals(chunkSize, foundUploadSession.chunkSize());
        assertEquals(lastChunkSize, foundUploadSession.lastChunkSize());
        // endregion
    }


    @Test
    void findBySha256() {
        // region Given
        long id = 5;
        UploadSessionState state = UploadSessionState.PROCESSING;
        String sha256 = "1350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c6";
        long size = 214;
        int chunkCount = 7;
        int chunkSize = 32;
        int lastChunkSize = 22;
        // endregion


        // region When
        UploadSession foundUploadSession = uploadSessionRepository.findBySha256(sha256);
        // endregion


        // region Then
        assertNotNull(foundUploadSession);
        assertEquals(id, foundUploadSession.id());
        assertEquals(state, foundUploadSession.state());
        assertEquals(sha256, foundUploadSession.sha256());
        assertEquals(size, foundUploadSession.size());
        assertEquals(chunkCount, foundUploadSession.chunkCount());
        assertEquals(chunkSize, foundUploadSession.chunkSize());
        assertEquals(lastChunkSize, foundUploadSession.lastChunkSize());
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "UPLOAD,     2",
            "PROCESSING, 1",
            "ERROR,      0"
    })
    void findAllIdByState(UploadSessionState state, int count) {
        // region Given
        // endregion


        // region When
        long[] allIdByState = uploadSessionRepository.findAllIdByState(state);
        // endregion


        // region Then
        assertNotNull(allIdByState);
        assertEquals(count, allIdByState.length);
        // endregion
    }


    @Test
    void insertUploadChunk() {
        // region Given
        UUID userUUID = UUID.randomUUID();
        long uploadSessionId = 7;
        long chunkId = 64;
        int chunkNumber = 4;
        UploadSessionState state = UploadSessionState.UPLOAD;
        OffsetDateTime lastModified = OffsetDateTime.now();
        // endregion

        // region When
        boolean insertFlag = uploadSessionRepository.insertUploadChunk(userUUID, uploadSessionId, chunkId, chunkNumber, state, lastModified);
        // endregion

        // region Then
        assertTrue(insertFlag);
        // endregion
    }


    @Test
    void updateUploadChunkState() {
        // region Given
        UUID userUUID = UUID.fromString("596aeb50-27a7-4361-ab12-e28ac6a4760c");
        long uploadSessionId = 7;
        int chunkNumber = 3;
        UploadSessionState newState = UploadSessionState.UPLOAD;
        OffsetDateTime lastModified = OffsetDateTime.now();
        // endregion


        // region When
        boolean updateFlag = uploadSessionRepository.updateUploadChunkState(
                userUUID, uploadSessionId, chunkNumber, newState, lastModified);
        // endregion


        // region Then
        assertTrue(updateFlag);
        // endregion
    }


    @Test
    void deleteUploadChunks1() {
        // region Given
        long uploadSessionId = 5;
        // endregion


        // region When
        boolean deleteFlag = uploadSessionRepository.deleteUploadChunks(uploadSessionId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        // endregion
    }


    @Test
    void deleteUploadChunks2() {
        // region Given
        long uploadSessionId = 5;
        UploadSessionState state = UploadSessionState.PROCESSING;
        OffsetDateTime lastModified = OffsetDateTime.now();
        // endregion


        // region When
        boolean deleteFlag = uploadSessionRepository.deleteUploadChunks(state, lastModified);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "4, 0",
            "5, 7",
            "7, 3"
    })
    void findChunkNumbers(long uploadSessionId, int chunkCount) {
        // region Given
        // endregion


        // region When
        int[] chunkNumbers = uploadSessionRepository.findChunkNumbers(uploadSessionId);
        // endregion


        // region Then
        assertNotNull(chunkNumbers);
        assertEquals(chunkCount, chunkNumbers.length);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "4, UPLOAD,     0",
            "4, PROCESSING, 0",
            "5, UPLOAD,     0",
            "5, PROCESSING, 7",
            "7, UPLOAD,     1",
            "7, PROCESSING, 2"
    })
    void countChunkNumbers(long uploadSessionId, UploadSessionState state, int expectedCount) {
        // region Given
        // endregion


        // region When
        int foundCount = uploadSessionRepository.countChunkNumbers(uploadSessionId, state);
        // endregion


        // region Then
        assertEquals(expectedCount, foundCount);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 1, 51",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 2, 52",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 3, 53",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 4, 54",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 5, 55",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 6, 56",
            "fea55e01-e825-4c17-9518-5c8418926029, 5, 7, 57",
            "596aeb50-27a7-4361-ab12-e28ac6a4760c, 7, 1, 61",
            "596aeb50-27a7-4361-ab12-e28ac6a4760c, 7, 2, 62",
            "596aeb50-27a7-4361-ab12-e28ac6a4760c, 7, 3, 63"
    })
    void findChunkId(UUID userUUID, long uploadSessionId, int number, long expectedChunkId) {
        // region Given
        // endregion


        // region When
        Long foundChunkId = uploadSessionRepository.findChunkId(userUUID, uploadSessionId, number);
        // endregion


        // region Then
        assertNotNull(foundChunkId);
        assertEquals(expectedChunkId, foundChunkId);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "5, 1, jDtfw0ackTV7voJilcywvcV0BPPt4DMOBIwcTN1QBuE=",
            "5, 2, YpXriML4NngZOtAVKIRUAmYWdxyw7yj4IBYlkYriQM8=",
            "5, 3, VkZxervyWWOdAajCxYyTPfs4j5N1frYRj3F9jNrdBH8=",
            "5, 4, ZW/MO7aAcQwagAmR40XW/TN8ip+LYcLAZfTU4iB+nQQ=",
            "5, 5, EihQoG4nxlnFZOAHTmqGioPat463KtKpGtcEpj87wfw=",
            "5, 6, 4ErVGVIMeh15gxPep6YXO6C9Vjqy43EoC51c0jNI/eE=",
            "5, 7, 2VEmSQXYkME0RV8d338cRQYjlAqpmA==",
            "7, 1, WtHrRi8IdXN50S+b5u+0MKGQ2Mv2vvvivEtLK4aU9qc=",
            "7, 2, mCtWVyDY0qV2YjggeSZDXwP9ORfh96/tZWOWj/uDeEA="
    })
    void findChunkContent(long uploadSessionId, int chunkNumber, String expectedChunkContent) {
        // region Given
        // endregion


        // region When
        String foundChunkContent = uploadSessionRepository.findChunkContent(uploadSessionId, chunkNumber);
        // endregion


        // region Then
        assertNotNull(foundChunkContent);
        assertEquals(expectedChunkContent, foundChunkContent);
        // endregion
    }

}

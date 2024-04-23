package ru.msvdev.ds.server.data.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.file.FileHandle;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileHandleRepositoryTest extends ApplicationTest {

    private final FileHandleRepository fileHandleRepository;
    private final ChunkRepository chunkRepository;

    @Autowired
    public FileHandleRepositoryTest(FileHandleRepository fileHandleRepository, ChunkRepository chunkRepository) {
        this.fileHandleRepository = fileHandleRepository;
        this.chunkRepository = chunkRepository;
    }

    private ChunkingSchema chunkingSchema;
    private long[] chunkIdArray;


    @BeforeEach
    void setUp() {
        long size = 1024 * 1024 * 4 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;

        chunkingSchema = ChunkingSchema.of(size, chunkSize, minChunkSize);
        assertTrue(chunkingSchema.isValid());
        System.out.println(chunkingSchema);

        chunkIdArray = new long[chunkingSchema.count()];
        for (int i = 0; i < chunkingSchema.count() - 1; i++) {
            Long chunkId = chunkRepository.insert(chunkingSchema.chunkSize(), "content");
            assertNotNull(chunkId);
            chunkIdArray[i] = chunkId;
        }
        Long chunkId = chunkRepository.insert(chunkingSchema.lastChunkSize(), "content");
        assertNotNull(chunkId);
        chunkIdArray[chunkingSchema.count() - 1] = chunkId;
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {
        String sha256 = "250d1c665ad54084191353cafbf502c8dad84ed659a804143a00335d790a6996";
        String mimeType = "image/jpeg";
        long size = chunkingSchema.size();
        int chunkCount = chunkingSchema.count();
        int chunkSize = chunkingSchema.chunkSize();
        int lastChunkSize = chunkingSchema.lastChunkSize();


        // = insert =============================
        Long fileHandleId = fileHandleRepository.insert(
                sha256, mimeType, size, chunkCount, chunkSize, lastChunkSize
        );

        assertNotNull(fileHandleId);


        // = findById ===========================
        Optional<FileHandle> optionalFileHandleById = fileHandleRepository.findById(fileHandleId);

        assertTrue(optionalFileHandleById.isPresent());
        FileHandle fileHandleById = optionalFileHandleById.get();
        assertEquals(fileHandleId, fileHandleById.id());
        assertEquals(sha256, fileHandleById.sha256());
        assertEquals(mimeType, fileHandleById.mimeType());
        assertEquals(size, fileHandleById.size());
        assertEquals(chunkCount, fileHandleById.chunkCount());
        assertEquals(chunkSize, fileHandleById.chunkSize());
        assertEquals(lastChunkSize, fileHandleById.lastChunkSize());


        // = findBySha256 =======================
        Optional<FileHandle> optionalFileHandleBySha256 = fileHandleRepository.findBySha256(sha256);

        assertTrue(optionalFileHandleBySha256.isPresent());
        FileHandle fileHandleBySha256 = optionalFileHandleBySha256.get();
        assertEquals(fileHandleId, fileHandleBySha256.id());
        assertEquals(sha256, fileHandleBySha256.sha256());
        assertEquals(mimeType, fileHandleBySha256.mimeType());
        assertEquals(size, fileHandleBySha256.size());
        assertEquals(chunkCount, fileHandleBySha256.chunkCount());
        assertEquals(chunkSize, fileHandleBySha256.chunkSize());
        assertEquals(lastChunkSize, fileHandleBySha256.lastChunkSize());


        // = findIdBySha256 =====================
        Long fileHandleIdBySha256 = fileHandleRepository.findIdBySha256(sha256);

        assertNotNull(fileHandleIdBySha256);
        assertEquals(fileHandleId, fileHandleIdBySha256);


        // = existSha256 ========================
        assertTrue(fileHandleRepository.existSha256(sha256));
    }


    @Test
    void chunkTest() {
        String sha256 = "250d1c665ad54084191353cafbf502c8dad84ed659a804143a00335d790a6996";
        String mimeType = "image/jpeg";
        long size = chunkingSchema.size();
        int chunkCount = chunkingSchema.count();
        int chunkSize = chunkingSchema.chunkSize();
        int lastChunkSize = chunkingSchema.lastChunkSize();


        // = insert =============================
        Long fileHandleId = fileHandleRepository.insert(
                sha256, mimeType, size, chunkCount, chunkSize, lastChunkSize
        );

        assertNotNull(fileHandleId);


        // = insertChunk ========================
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(
                    fileHandleRepository.insertChunk(fileHandleId, chunkIdArray[i], i + 1)
            );
        }


        // = existsChunk ========================
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(
                    fileHandleRepository.existsChunk(fileHandleId, chunkIdArray[i])
            );
        }


        // = existsChunkNumber ==================
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(
                    fileHandleRepository.existsChunkNumber(fileHandleId, i + 1)
            );
        }


        // = findChunkNumbers ===================
        List<Integer> chunkNumbers = fileHandleRepository.findChunkNumbers(fileHandleId);
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(chunkNumbers.contains(i + 1));
        }


        // = deleteChunk ========================
        assertTrue(fileHandleRepository.deleteChunk(fileHandleId, chunkIdArray[0]));

        assertFalse(fileHandleRepository.existsChunk(fileHandleId, chunkIdArray[0]));


        // = deleteAllChunks ====================
        assertTrue(fileHandleRepository.deleteAllChunks(fileHandleId));

        assertTrue(fileHandleRepository.findChunkNumbers(fileHandleId).isEmpty());

    }


    @Test
    void folderHandleTest() {
        Optional<FileHandle> folderHandlerOptional = fileHandleRepository.findFolderHandle();

        assertTrue(folderHandlerOptional.isPresent());

        FileHandle folderHandle = folderHandlerOptional.get();
        System.out.println("Folder Handle ID: " + folderHandle.id());

        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", folderHandle.sha256());
        assertEquals("inode/directory", folderHandle.mimeType());
        assertEquals(0, folderHandle.chunkCount());
        assertEquals(0, folderHandle.size());
        assertEquals(0, folderHandle.chunkSize());
        assertEquals(0, folderHandle.lastChunkSize());
    }

}

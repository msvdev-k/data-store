package ru.msvdev.ds.server.data.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.file.Chunk;
import ru.msvdev.ds.server.data.entity.file.ContainerHeader;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerRepositoryTest extends ApplicationTest {

    private final ContainerRepository containerRepository;
    private final ChunkRepository chunkRepository;

    @Autowired
    public ContainerRepositoryTest(ContainerRepository containerRepository, ChunkRepository chunkRepository) {
        this.containerRepository = containerRepository;
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
        long size = chunkingSchema.size();
        int chunkCount = chunkingSchema.count();
        int chunkSize = chunkingSchema.chunkSize();
        int lastChunkSize = chunkingSchema.lastChunkSize();


        // = insert =============================
        Long containerId = containerRepository.insert(
                sha256, size, chunkCount, chunkSize, lastChunkSize
        );

        assertNotNull(containerId);


        // = findById ===========================
        ContainerHeader containerHeaderById = containerRepository.findById(containerId);

        assertNotNull(containerHeaderById);
        assertEquals(containerId, containerHeaderById.id());
        assertEquals(sha256, containerHeaderById.sha256());
        assertEquals(size, containerHeaderById.size());
        assertEquals(chunkCount, containerHeaderById.chunkCount());
        assertEquals(chunkSize, containerHeaderById.chunkSize());
        assertEquals(lastChunkSize, containerHeaderById.lastChunkSize());


        // = findBySha256 =======================
        ContainerHeader containerHeaderBySha256 = containerRepository.findBySha256(sha256);

        assertNotNull(containerHeaderBySha256);
        assertEquals(containerId, containerHeaderBySha256.id());
        assertEquals(sha256, containerHeaderBySha256.sha256());
        assertEquals(size, containerHeaderBySha256.size());
        assertEquals(chunkCount, containerHeaderBySha256.chunkCount());
        assertEquals(chunkSize, containerHeaderBySha256.chunkSize());
        assertEquals(lastChunkSize, containerHeaderBySha256.lastChunkSize());


        // = findIdBySha256 =====================
        Long containerIdBySha256 = containerRepository.findIdBySha256(sha256);

        assertNotNull(containerIdBySha256);
        assertEquals(containerId, containerIdBySha256);


        // = existSha256 ========================
        assertTrue(containerRepository.existSha256(sha256));
    }


    @Test
    void insertFindChunkTest() {
        String sha256 = "250d1c665ad54084191353cafbf502c8dad84ed659a804143a00335d790a6996";
        long size = chunkingSchema.size();
        int chunkCount = chunkingSchema.count();
        int chunkSize = chunkingSchema.chunkSize();
        int lastChunkSize = chunkingSchema.lastChunkSize();

        // = insert =============================
        Long containerId = containerRepository.insert(sha256, size, chunkCount, chunkSize, lastChunkSize);
        assertNotNull(containerId);


        // = insertChunk ========================
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(
                    containerRepository.insertChunk(containerId, chunkIdArray[i], i + 1)
            );
        }


        // = findChunk ==========================
        for (int i = 0; i < chunkCount; i++) {
            int chunkNumber = i + 1;
            Chunk chunk = containerRepository.findChunk(containerId, chunkNumber);

            assertNotNull(chunk);
            assertEquals(chunkingSchema.getChunkSize(chunkNumber), chunk.size());
            assertEquals("content", chunk.content());
            assertEquals(chunkNumber, chunk.number());
        }
    }


    @Test
    void findEmptyContainerTest() {
        ContainerHeader emptyContainer = containerRepository.findEmptyContainer();

        assertNotNull(emptyContainer);

        System.out.println("Empty Container ID: " + emptyContainer.id());

        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", emptyContainer.sha256());
        assertEquals(0, emptyContainer.chunkCount());
        assertEquals(0, emptyContainer.size());
        assertEquals(0, emptyContainer.chunkSize());
        assertEquals(0, emptyContainer.lastChunkSize());
    }


    @Test
    void deleteByIdTest() {
        String sha256 = "250d1c665ad54084191353cafbf502c8dad84ed659a804143a00335d790a6996";
        long size = chunkingSchema.size();
        int chunkCount = chunkingSchema.count();
        int chunkSize = chunkingSchema.chunkSize();
        int lastChunkSize = chunkingSchema.lastChunkSize();

        // = insert =============================
        Long containerId = containerRepository.insert(sha256, size, chunkCount, chunkSize, lastChunkSize);
        assertNotNull(containerId);

        // = insertChunk ========================
        for (int i = 0; i < chunkCount; i++) {
            assertTrue(containerRepository.insertChunk(containerId, chunkIdArray[i], i + 1));
        }


        // = deleteById =========================
        assertTrue(
                containerRepository.deleteById(containerId)
        );


        // проверка удаления фрагментов
        assertNull(containerRepository.findById(containerId));
        for (int i = 0; i < chunkCount; i++) {
            int chunkNumber = i + 1;
            assertNull(containerRepository.findChunk(containerId, chunkNumber));
            assertNull(chunkRepository.findById(chunkIdArray[i]));
        }

    }

}

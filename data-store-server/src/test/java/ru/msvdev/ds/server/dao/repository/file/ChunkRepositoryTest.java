package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.file.Chunk;

import java.util.Base64;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChunkRepositoryTest extends ApplicationTest {

    private final ChunkRepository chunkRepository;

    @Autowired
    public ChunkRepositoryTest(ChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {

        int size = 1024 * 1024; // 1M

        byte[] bytes = new byte[size];
        Random rnd = new Random();
        rnd.nextBytes(bytes);
        String content = Base64.getEncoder().encodeToString(bytes);


        // = insert =====================

        Long chunkId = chunkRepository.insert(size, content);

        assertNotNull(chunkId);


        // = findById ===================

        Chunk chunk1 = chunkRepository.findById(chunkId);

        assertNotNull(chunk1);
        assertEquals(size, chunk1.size());
        assertEquals(content, chunk1.content());
        assertEquals(-1, chunk1.number());


        // = updateContent ==============
        rnd.nextBytes(bytes);
        String content2 = Base64.getEncoder().encodeToString(bytes);
        assertNotEquals(content, content2);

        assertTrue(chunkRepository.updateContent(chunkId, content2));

        Chunk chunk2 = chunkRepository.findById(chunkId);
        assertNotNull(chunk2);
        assertEquals(size, chunk2.size());
        assertEquals(content2, chunk2.content());
        assertEquals(-1, chunk2.number());


        // = deleteById =================

        assertTrue(chunkRepository.deleteById(chunkId));

        Chunk chunk3 = chunkRepository.findById(chunkId);
        assertNull(chunk3);
    }

}
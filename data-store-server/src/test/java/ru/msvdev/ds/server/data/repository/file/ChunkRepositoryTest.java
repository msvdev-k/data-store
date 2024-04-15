package ru.msvdev.ds.server.data.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.file.FileChunk;

import java.util.Base64;
import java.util.Optional;
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

        Optional<FileChunk> optionalFileChunk = chunkRepository.findById(chunkId);

        assertTrue(optionalFileChunk.isPresent());
        assertEquals(size, optionalFileChunk.get().size());
        assertEquals(content, optionalFileChunk.get().content());
        assertNull(optionalFileChunk.get().number());


        // = updateContent ==============
        rnd.nextBytes(bytes);
        String content2 = Base64.getEncoder().encodeToString(bytes);
        assertNotEquals(content, content2);

        assertTrue(chunkRepository.updateContent(chunkId, content2));

        Optional<FileChunk> optionalFileChunk2 = chunkRepository.findById(chunkId);
        assertTrue(optionalFileChunk2.isPresent());
        assertEquals(size, optionalFileChunk2.get().size());
        assertEquals(content2, optionalFileChunk2.get().content());
        assertNull(optionalFileChunk2.get().number());


        // = deleteById =================

        assertTrue(chunkRepository.deleteById(chunkId));

        Optional<FileChunk> optionalFileChunk3 = chunkRepository.findById(chunkId);
        assertTrue(optionalFileChunk3.isEmpty());
    }

}
package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/file/container-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerChunkRepositoryTest extends ApplicationTest {

    private final ContainerRepository containerRepository;
    private final ChunkRepository chunkRepository;

    @Autowired
    public ContainerChunkRepositoryTest(ContainerRepository containerRepository, ChunkRepository chunkRepository) {
        this.containerRepository = containerRepository;
        this.chunkRepository = chunkRepository;
    }


    @Test
    void containerRepository_DeleteById() {
        // region Given
        long containerId = 20;

        long[] chunkIds = new long[]{21, 22, 23, 24};
        for (long chunkId : chunkIds) {
            assertNotNull(chunkRepository.findContent(chunkId));
        }
        // endregion


        // region When
        boolean deleteFlag = containerRepository.deleteById(containerId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        assertNull(containerRepository.findById(containerId));

        for (long chunkId : chunkIds) {
            assertNull(chunkRepository.findContent(chunkId));
        }
        // endregion
    }

}

package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/file/chunk-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChunkRepositoryTest extends ApplicationTest {

    private final ChunkRepository chunkRepository;

    @Autowired
    public ChunkRepositoryTest(ChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }


    @ParameterizedTest
    @CsvSource({
            "1, aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI=",
            "2, aXVuZmpHc2RoBmg4NzNneTRyYjNmYyA=",
            "3, null"
    })
    void findContent(long id, String content) {
        // region Given
        if (content.equals("null")) content = null;
        // endregion


        // region When
        String fountContent = chunkRepository.findContent(id);
        // endregion


        // region Then
        if (content != null) {
            assertNotNull(fountContent);
            assertEquals(content, fountContent);
        } else {
            assertNull(fountContent);
        }
        // endregion
    }


    @Test
    void insertContent() {
        // region Given
        long id = 37;
        String content = "dmZnZg==";
        // endregion


        // region When
        Long insertedId = chunkRepository.insertContent(content);
        // endregion


        // region Then
        assertNotNull(insertedId);
        assertEquals(id, insertedId);

        assertEquals(content, chunkRepository.findContent(id));
        // endregion
    }


    @Test
    void updateContent() {
        // region Given
        long id = 1;
        String newContent = "dmZnZg==";
        // endregion


        // region When
        boolean updateFlag = chunkRepository.updateContent(id, newContent);
        // endregion


        // region Then
        assertTrue(updateFlag);
        assertEquals(newContent, chunkRepository.findContent(id));
        // endregion
    }

}
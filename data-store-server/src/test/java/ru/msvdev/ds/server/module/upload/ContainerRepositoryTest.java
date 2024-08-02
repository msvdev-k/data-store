package ru.msvdev.ds.server.module.upload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.upload.entity.ContainerHeader;
import ru.msvdev.ds.server.module.upload.repository.ContainerRepository;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/upload/container-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerRepositoryTest extends ApplicationTest {

    private final ContainerRepository containerRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ContainerRepositoryTest(ContainerRepository containerRepository, JdbcTemplate jdbcTemplate) {
        this.containerRepository = containerRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Test
    void findById() {
        // region Given
        long id = 20;
        String sha256 = "eab0053353806d62467da0b5719254c3806887ecbca61ccb65cda790f48b0252";
        long size = 134;
        int chunkCount = 4;
        int chunkSize = 32;
        int lastChunkSize = 38;
        // endregion


        // region When
        ContainerHeader containerHeader = containerRepository.findById(id);
        // endregion


        // region Then
        assertNotNull(containerHeader);
        assertEquals(id, containerHeader.id());
        assertEquals(sha256, containerHeader.sha256());
        assertEquals(size, containerHeader.size());
        assertEquals(chunkCount, containerHeader.chunkCount());
        assertEquals(chunkSize, containerHeader.chunkSize());
        assertEquals(lastChunkSize, containerHeader.lastChunkSize());
        // endregion
    }


    @Test
    void findBySha256() {
        // region Given
        long id = 20;
        String sha256 = "eab0053353806d62467da0b5719254c3806887ecbca61ccb65cda790f48b0252";
        long size = 134;
        int chunkCount = 4;
        int chunkSize = 32;
        int lastChunkSize = 38;
        // endregion


        // region When
        ContainerHeader containerHeader = containerRepository.findBySha256(sha256);
        // endregion


        // region Then
        assertNotNull(containerHeader);
        assertEquals(id, containerHeader.id());
        assertEquals(sha256, containerHeader.sha256());
        assertEquals(size, containerHeader.size());
        assertEquals(chunkCount, containerHeader.chunkCount());
        assertEquals(chunkSize, containerHeader.chunkSize());
        assertEquals(lastChunkSize, containerHeader.lastChunkSize());
        // endregion
    }


    @Test
    void insertFromUploadSession() {
        // region Given
        long uploadSessionId = 5;
        String sha256 = "1350b3ccbac695c62f55787f54fb60e838c8f4767afe4d5a577ecb96a1ec19c6";
        long size = 214;
        int chunkCount = 7;
        int chunkSize = 32;
        int lastChunkSize = 22;

        long expectedContainerId = 37;

        String[] expectedContents = new String[]{
                "jDtfw0ackTV7voJilcywvcV0BPPt4DMOBIwcTN1QBuE=",
                "YpXriML4NngZOtAVKIRUAmYWdxyw7yj4IBYlkYriQM8=",
                "VkZxervyWWOdAajCxYyTPfs4j5N1frYRj3F9jNrdBH8=",
                "ZW/MO7aAcQwagAmR40XW/TN8ip+LYcLAZfTU4iB+nQQ=",
                "EihQoG4nxlnFZOAHTmqGioPat463KtKpGtcEpj87wfw=",
                "4ErVGVIMeh15gxPep6YXO6C9Vjqy43EoC51c0jNI/eE=",
                "2VEmSQXYkME0RV8d338cRQYjlAqpmA=="
        };
        // endregion


        // region When
        boolean insertFlag = containerRepository.insertFromUploadSession(uploadSessionId);
        // endregion


        // region Then
        assertTrue(insertFlag);

        ContainerHeader containerHeader = containerRepository.findBySha256(sha256);

        assertEquals(expectedContainerId, containerHeader.id());
        assertEquals(sha256, containerHeader.sha256());
        assertEquals(size, containerHeader.size());
        assertEquals(chunkCount, containerHeader.chunkCount());
        assertEquals(chunkSize, containerHeader.chunkSize());
        assertEquals(lastChunkSize, containerHeader.lastChunkSize());

        for (int i = 0; i < chunkCount; i++) {
            String query = String.format("""
                    SELECT ch.content
                    FROM container_chunks AS cnt
                    INNER JOIN chunks AS ch ON ch.id = cnt.chunk_id
                    WHERE cnt.container_id = %d AND cnt.number = %d
                    """, expectedContainerId, i + 1);

            String actualContent = jdbcTemplate.queryForObject(query, String.class);
            assertEquals(expectedContents[i], actualContent);
        }
        // endregion
    }

}

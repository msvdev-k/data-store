package ru.msvdev.ds.server.dao.repository.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.file.ContainerHeader;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/file/container-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContainerRepositoryTest extends ApplicationTest {

    private final ContainerRepository containerRepository;

    @Autowired
    public ContainerRepositoryTest(ContainerRepository containerRepository) {
        this.containerRepository = containerRepository;
    }


    @Test
    void findEmptyContainer() {
        // region Given
        String emptySha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        long size = 0;
        int chunkCount = 0;
        int chunkSize = 0;
        int lastChunkSize = 0;
        // endregion


        // region When
        ContainerHeader emptyContainer = containerRepository.findEmptyContainer();
        // endregion


        // region Then
        assertNotNull(emptyContainer);
        assertEquals(emptySha256, emptyContainer.sha256());
        assertEquals(size, emptyContainer.size());
        assertEquals(chunkCount, emptyContainer.chunkCount());
        assertEquals(chunkSize, emptyContainer.chunkSize());
        assertEquals(lastChunkSize, emptyContainer.lastChunkSize());

        System.out.println("Empty Container ID: " + emptyContainer.id());
        // endregion

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

        long containerId = 37;

        String[] chunkStrings = new String[]{
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
        assertEquals(containerId, containerHeader.id());
        assertEquals(sha256, containerHeader.sha256());
        assertEquals(size, containerHeader.size());
        assertEquals(chunkCount, containerHeader.chunkCount());
        assertEquals(chunkSize, containerHeader.chunkSize());
        assertEquals(lastChunkSize, containerHeader.lastChunkSize());

        for (int i = 0; i < chunkCount; i++) {
            assertEquals(chunkStrings[i], containerRepository.findChunkContent(containerId, i + 1));
        }
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "20, 0, null",
            "20, 1, 4UH4+gyc0GV6IoLnxN96hcQH2kmmWg/vxysxXKu4VFE=",
            "20, 2, wHj7vIJkq61M79rZqLnJy3DaRe2mOM3cMZI2ghq1H/I=",
            "20, 3, rWG4uz5U7yjPs+r2+kPjVw+CqdbTpc3f3jLyLy0OHu4=",
            "20, 4, szFP1rx//PPSoKW7GcQ43T4loXTUvdfVgQde0nudCooiSFRkbBI=",
            "20, 5, null"
    })
    void findChunkContent(long containerId, int chunkNumber, String expectedContent) {
        // region Given
        if (expectedContent.equals("null")) expectedContent = null;
        // endregion


        // region When
        String foundContent = containerRepository.findChunkContent(containerId, chunkNumber);
        // endregion


        // region Then
        assertEquals(expectedContent, foundContent);
        // endregion
    }


    @Test
    void deleteById() {
        // region Given
        long containerId = 20;
        // endregion


        // region When
        boolean deleteFlag = containerRepository.deleteById(containerId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        assertNull(containerRepository.findById(containerId));
        // endregion
    }

}

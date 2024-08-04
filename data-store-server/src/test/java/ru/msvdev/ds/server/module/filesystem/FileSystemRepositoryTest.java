package ru.msvdev.ds.server.module.filesystem;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.filesystem.entity.FileInfo;
import ru.msvdev.ds.server.module.filesystem.repository.FileSystemRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJdbcTest
@Sql(
        value = {"classpath:module/filesystem/file-system-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileSystemRepositoryTest extends ApplicationTest {

    private final FileSystemRepository fileSystemRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileSystemRepositoryTest(FileSystemRepository fileSystemRepository, JdbcTemplate jdbcTemplate) {
        this.fileSystemRepository = fileSystemRepository;
        this.jdbcTemplate = jdbcTemplate;
    }


    private static long catalogId;
    private static OffsetDateTime offsetDateTimeForTest;


    @BeforeAll
    static void beforeAll() {
        catalogId = 110;
        offsetDateTimeForTest = OffsetDateTime.of(
                2024, 7, 12, 17, 23, 11, 0, ZoneOffset.UTC
        );
    }


    @Test
    void insertRoot() {
        // region Given
        long catalogId = 111;
        FileInfo rootInfo = new FileInfo(
                147, "$ROOT$", "inode/directory", offsetDateTimeForTest, 0
        );
        // endregion


        // region When
        boolean insertedFlag = fileSystemRepository.insertRoot(catalogId, offsetDateTimeForTest);
        // endregion


        // region Then
        assertTrue(insertedFlag);

        FileInfo fountRootInfo = fileSystemRepository.findById(catalogId, rootInfo.id());
        assertEquals(rootInfo, fountRootInfo);
        // endregion
    }


    @Test
    void insertDuplicateRootTest() {
        // region Given
        // endregion


        // region When
        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileSystemRepository.insertRoot(catalogId, OffsetDateTime.now())
        );
        // endregion


        // region Then
        // endregion
    }


    @Test
    void insertFolderToRoot() {
        // region Given
        FileInfo folderInfo = new FileInfo(
                147, "folder", "inode/directory", offsetDateTimeForTest, 0
        );
        // endregion


        // region When
        FileInfo insertedFolder = fileSystemRepository.insertFolder(catalogId, folderInfo.name(), offsetDateTimeForTest);
        // endregion


        // region Then
        assertTrue(insertedFolder.isDirectory());
        assertEquals(folderInfo, insertedFolder);
        // endregion
    }

    @Test
    void insertDuplicateFolderToRootTest() {
        // region Given
        String folderName = "диреКТорИЯ 1";
        // endregion


        // region When
        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileSystemRepository.insertFolder(catalogId, folderName, OffsetDateTime.now())
        );
        // endregion


        // region Then
        // endregion
    }


    @ParameterizedTest
    @ValueSource(longs = {40, 41, 44, 45})
    void insertFolderToFolder(long parentFolderId) {
        // region Given
        String folderName = "folder";
        FileInfo folderInfo = new FileInfo(
                147, folderName, "inode/directory", offsetDateTimeForTest, 0
        );
        // endregion


        // region When
        FileInfo insertedFolder = fileSystemRepository.insertFolder(catalogId, parentFolderId, folderName, offsetDateTimeForTest);
        // endregion


        // region Then
        assertTrue(insertedFolder.isDirectory());
        assertEquals(folderInfo, insertedFolder);
        // endregion
    }

    @Test
    void insertDuplicateFolderToFolderTest() {
        // region Given
        long parentFolderId = 41;
        String folderName = "ДирекТОРИя 4";
        // endregion


        // region When
        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileSystemRepository.insertFolder(catalogId, parentFolderId, folderName, OffsetDateTime.now())
        );
        // endregion


        // region Then
        // endregion
    }


    @Test
    void insertFileToRoot() {
        // region Given
        long containerId = 105;

        FileInfo fileInfo = new FileInfo(
                147, "File Name", "application/pdf", offsetDateTimeForTest, 136
        );
        // endregion


        // region When
        FileInfo insertedFile = fileSystemRepository.insertFile(
                catalogId, fileInfo.name(), containerId, fileInfo.mimeType(), offsetDateTimeForTest
        );
        // endregion


        // region Then
        assertTrue(insertedFile.isFile());
        assertEquals(fileInfo, insertedFile);
        // endregion
    }

    @Test
    void insertDuplicateFileToRootTest() {
        // region Given
        long containerId = 105;
        String fileName = "ФАЙЛ 1";
        // endregion


        // region When
        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileSystemRepository.insertFile(catalogId, fileName, containerId, "", OffsetDateTime.now())
        );
        // endregion


        // region Then
        // endregion
    }


    @ParameterizedTest
    @ValueSource(longs = {40, 41, 44, 45})
    void insertFileToFolder(long parentFolderId) {
        // region Given
        long containerId = 105;

        FileInfo fileInfo = new FileInfo(
                147, "File Name", "application/pdf", offsetDateTimeForTest, 136
        );
        // endregion


        // region When
        FileInfo insertedFile = fileSystemRepository.insertFile(
                catalogId, parentFolderId, fileInfo.name(), containerId, fileInfo.mimeType(), offsetDateTimeForTest
        );
        // endregion


        // region Then
        assertTrue(insertedFile.isFile());
        assertEquals(fileInfo, insertedFile);
        // endregion
    }

    @Test
    void insertDuplicateFileToFolderTest() {
        // region Given
        long parentFolderId = 41;
        long containerId = 105;
        String fileName = "ФАЙЛ 4";
        // endregion


        // region When
        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileSystemRepository.insertFile(catalogId, parentFolderId, fileName, containerId, "", OffsetDateTime.now())
        );
        // endregion


        // region Then
        // endregion
    }


    @ParameterizedTest
    @MethodSource
    void findById(FileInfo expectedFileInfo) {
        // region Given
        long nodeId = expectedFileInfo.id();
        // endregion


        // region When
        FileInfo fileInfo = fileSystemRepository.findById(catalogId, nodeId);
        // endregion


        // region Then
        assertEquals(expectedFileInfo, fileInfo);
        // endregion
    }

    private static Stream<Arguments> findById() {
        return Stream.of(
                Arguments.of(new FileInfo(40, "Директория 1", "inode/directory", offsetDateTimeForTest, 0)),
                Arguments.of(new FileInfo(41, "Директория 2", "inode/directory", offsetDateTimeForTest, 0)),
                Arguments.of(new FileInfo(42, "Файл 1", "application/pdf", offsetDateTimeForTest, 131)),
                Arguments.of(new FileInfo(43, "Файл 2", "application/xml", offsetDateTimeForTest, 132)),
                Arguments.of(new FileInfo(44, "Директория 3", "inode/directory", offsetDateTimeForTest, 0)),
                Arguments.of(new FileInfo(45, "Директория 4", "inode/directory", offsetDateTimeForTest, 0)),
                Arguments.of(new FileInfo(46, "Файл 3", "application/pdf", offsetDateTimeForTest, 133)),
                Arguments.of(new FileInfo(47, "Файл 4", "application/zip", offsetDateTimeForTest, 134)),
                Arguments.of(new FileInfo(48, "Файл 5", "application/xml", offsetDateTimeForTest, 135))
        );
    }


    @ParameterizedTest
    @ValueSource(longs = {40, 41, 42, 43, 44, 45, 46, 47, 48})
    void rename(long nodeId) {
        // region Given
        String newName = "Новое называние объекта файловой системы";
        String uNewName = newName.toUpperCase();
        // endregion


        // region When
        boolean renameFlag = fileSystemRepository.rename(catalogId, nodeId, newName);
        // endregion


        // region Then
        assertTrue(renameFlag);

        String query = String.format("SELECT uname FROM files WHERE id = %d", nodeId);
        String actualUName = jdbcTemplate.queryForObject(query, String.class);
        assertEquals(uNewName, actualUName);
        // endregion
    }


    @ParameterizedTest
    @MethodSource
    void remove(long nodeId, Set<Integer> existingNodeIdSet) {
        // region Given
        // endregion


        // region When
        boolean removeFlag = fileSystemRepository.remove(catalogId, nodeId);
        // endregion


        // region Then
        assertTrue(removeFlag);

        String query = String.format("SELECT id FROM files WHERE catalog_id = %d AND uname != '$ROOT$'", catalogId);
        List<Integer> actualNodeIdList = jdbcTemplate.queryForList(query, Integer.class);
        assertEquals(existingNodeIdSet, new HashSet<>(actualNodeIdList));
        // endregion
    }

    private static Stream<Arguments> remove() {
        return Stream.of(
//                Arguments.of(30, Set.of()),
                Arguments.of(40, Set.of(41, 42, 43, 44, 45, 46, 47, 48)),
                Arguments.of(41, Set.of(40, 42, 43)),
                Arguments.of(42, Set.of(40, 41, 43, 44, 45, 46, 47, 48)),
                Arguments.of(43, Set.of(40, 41, 42, 44, 45, 46, 47, 48)),
                Arguments.of(44, Set.of(40, 41, 42, 43, 45, 46, 47, 48)),
                Arguments.of(45, Set.of(40, 41, 42, 43, 44, 46, 47, 48)),
                Arguments.of(46, Set.of(40, 41, 42, 43, 44, 45, 47, 48)),
                Arguments.of(47, Set.of(40, 41, 42, 43, 44, 45, 46, 48)),
                Arguments.of(48, Set.of(40, 41, 42, 43, 44, 45, 46, 47))
        );
    }


    @Test
    void findAllFromRoot() {
        // region Given
        Set<FileInfo> expectedFileSet = Set.of(
                new FileInfo(40, "Директория 1", "inode/directory", offsetDateTimeForTest, 0),
                new FileInfo(41, "Директория 2", "inode/directory", offsetDateTimeForTest, 0),
                new FileInfo(42, "Файл 1", "application/pdf", offsetDateTimeForTest, 131),
                new FileInfo(43, "Файл 2", "application/xml", offsetDateTimeForTest, 132)
        );
        // endregion


        // region When
        List<FileInfo> fileInfoList = fileSystemRepository.findAll(catalogId);
        // endregion


        // region Then
        assertEquals(expectedFileSet, new HashSet<>(fileInfoList));
        // endregion
    }


    @Test
    void findAll() {
        // region Given
        long folderId = 41;

        Set<FileInfo> expectedFileSet = Set.of(
                new FileInfo(44, "Директория 3", "inode/directory", offsetDateTimeForTest, 0),
                new FileInfo(45, "Директория 4", "inode/directory", offsetDateTimeForTest, 0),
                new FileInfo(46, "Файл 3", "application/pdf", offsetDateTimeForTest, 133),
                new FileInfo(47, "Файл 4", "application/zip", offsetDateTimeForTest, 134),
                new FileInfo(48, "Файл 5", "application/xml", offsetDateTimeForTest, 135)
        );
        //endregion


        // region When
        List<FileInfo> fileInfoList = fileSystemRepository.findAll(catalogId, folderId);
        // endregion


        // region Then
        assertEquals(expectedFileSet, new HashSet<>(fileInfoList));
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "100, eab0053353806d62467da0b5719254c3806887ec4ca61ccb65cda790f48b0252",
            "101, 36e9453c25c39342e1851b0303cd506ebc464b4965b961b3cfb3af2c63f63020",
            "102, 85dd0a6b0c3441156777b1015108496a86706a22f203c0f3a39214c109f957e8",
            "103, c7d2dcd005889157fc58a889722be6e4d8f4c84b1266268ab0b6e76dd8ec3476",
            "104, 80419563519d28c4c5382b323e8a7d5c31f78d9846b79a089142a3d4a9331990",
            "105, 0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990",
            "000, 0000000000000000000000000000000000000000000000000000000000000000"
    })
    void findContainerIdBySha256(long containerId, String sha256) {
        // region Given
        // endregion


        // region When
        Long containerIdBySha256 = fileSystemRepository.findContainerIdBySha256(sha256);
        // endregion


        // region Then
        if (containerId > 0) {
            assertNotNull(containerIdBySha256);
            assertEquals(containerId, containerIdBySha256);

        } else {
            assertNull(containerIdBySha256);
        }
        // endregion
    }

}

package ru.msvdev.ds.server.data.repository.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.Catalog;
import ru.msvdev.ds.server.data.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.data.entity.file.FileHandle;
import ru.msvdev.ds.server.data.entity.file.FileInfo;
import ru.msvdev.ds.server.data.repository.CatalogRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FileRepositoryTest extends ApplicationTest {

    private final FileRepository fileRepository;
    private final CatalogRepository catalogRepository;
    private final FileHandleRepository fileHandleRepository;

    @Autowired
    public FileRepositoryTest(FileRepository fileRepository, CatalogRepository catalogRepository, FileHandleRepository fileHandleRepository) {
        this.fileRepository = fileRepository;
        this.catalogRepository = catalogRepository;
        this.fileHandleRepository = fileHandleRepository;
    }

    private long catalogId;
    private FileHandle fileHandle;
    private ChunkingSchema fileChunking;


    @BeforeEach
    void setUp() {
        Catalog catalog = catalogRepository.insert("Catalog 1", "");
        catalogId = catalog.id();

        long size = 1024 * 1024 * 4 + 256 * 1024;
        int chunkSize = 1024 * 1024;
        int minChunkSize = 512 * 1024;
        fileChunking = ChunkingSchema.of(size, chunkSize, minChunkSize);

        long fileHandleId = fileHandleRepository.insert(
                "f5d343132f7ab1938e186ce599d75fca793022331deb73e544bddbe95538c742",
                "application/json",
                fileChunking.size(), fileChunking.count(), fileChunking.chunkSize(), fileChunking.lastChunkSize()
        );
        Optional<FileHandle> optionalFileHandle = fileHandleRepository.findById(fileHandleId);
        assertTrue(optionalFileHandle.isPresent());
        fileHandle = optionalFileHandle.get();
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void insertRootTest() {
        assertTrue(fileRepository.insertRoot(catalogId, OffsetDateTime.now()));

        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileRepository.insertRoot(catalogId, OffsetDateTime.now())
        );
    }


    @Test
    void insertFolderToRootTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());

        Long folderId = fileRepository.insertToRoot(catalogId, "folder", OffsetDateTime.now());
        assertNotNull(folderId);

        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileRepository.insertToRoot(catalogId, "FOLDER", OffsetDateTime.now())
        );
    }


    @Test
    void insertFileToRootTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());

        Long fileId = fileRepository.insertToRoot(catalogId, fileHandle.id(), "File Name", OffsetDateTime.now());
        assertNotNull(fileId);

        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileRepository.insertToRoot(catalogId, fileHandle.id(), "FILE Name", OffsetDateTime.now())
        );
    }


    @Test
    void insertFolderToFolderTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long parentFolderId = fileRepository.insertToRoot(catalogId, "parent folder", OffsetDateTime.now());

        Long folderId = fileRepository.insertToFolder(catalogId, parentFolderId, "child folder", OffsetDateTime.now());
        assertNotNull(folderId);

        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileRepository.insertToFolder(catalogId, parentFolderId, "child FOLDER", OffsetDateTime.now())
        );
    }


    @Test
    void insertFileToFolderTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long parentFolderId = fileRepository.insertToRoot(catalogId, "parent folder", OffsetDateTime.now());

        Long fileId = fileRepository.insertToFolder(catalogId, parentFolderId, fileHandle.id(), "File Name", OffsetDateTime.now());
        assertNotNull(fileId);

        assertThrowsExactly(DuplicateKeyException.class, () ->
                fileRepository.insertToFolder(catalogId, parentFolderId, fileHandle.id(), "file name", OffsetDateTime.now())
        );
    }


    @Test
    void findByIdTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        String fileName = "File Name";
        OffsetDateTime creationDate = OffsetDateTime.now();
        Long fileId = fileRepository.insertToRoot(catalogId, fileHandle.id(), fileName, creationDate);


        FileInfo fileInfo = fileRepository.findById(catalogId, fileId);
        assertNotNull(fileInfo);
        FileInfo rootInfo = fileRepository.findById(catalogId, fileInfo.folderId());
        assertNotNull(rootInfo);

        System.out.println(fileInfo);
        System.out.println(rootInfo);

        assertEquals(fileId, fileInfo.id());
        assertEquals(rootInfo.id(), fileInfo.folderId());
        assertEquals(fileName, fileInfo.name());
        assertEquals(fileHandle.mimeType(), fileInfo.mimeType());
        assertEquals(creationDate.toEpochSecond(), fileInfo.createDate().toEpochSecond());
        assertEquals(fileHandle.size(), fileInfo.size());
        assertFalse(fileInfo.isRoot());
        assertFalse(fileInfo.isDirectory());
        assertTrue(fileInfo.isFile());

        assertEquals(fileInfo.folderId(), rootInfo.id());
        assertEquals(-1, rootInfo.folderId());
        assertEquals("ROOT", rootInfo.name());
        assertTrue(rootInfo.isRoot());
        assertTrue(rootInfo.isDirectory());
        assertFalse(rootInfo.isFile());
    }


    @Test
    void findSha256ByIdTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long fileId = fileRepository.insertToRoot(catalogId, fileHandle.id(), "File Name", OffsetDateTime.now());

        String sha256 = fileRepository.findSha256ById(catalogId, fileId);
        assertNotNull(sha256);
        assertEquals(fileHandle.sha256(), sha256);
    }


    @Test
    void findChunkingSchemaByIdTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long fileId = fileRepository.insertToRoot(catalogId, fileHandle.id(), "File Name", OffsetDateTime.now());

        ChunkingSchema chunkingSchema = fileRepository.findChunkingSchemaById(catalogId, fileId);
        assertNotNull(chunkingSchema);
        assertEquals(fileChunking, chunkingSchema);
        assertTrue(chunkingSchema.isValid());
    }


    @Test
    void findAllFromRootTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long folderId = fileRepository.insertToRoot(catalogId, "Folder", OffsetDateTime.now());
        Long fileId = fileRepository.insertToRoot(catalogId, fileHandle.id(), "File Name", OffsetDateTime.now());

        List<FileInfo> fileInfoList = fileRepository.findAll(catalogId);
        assertEquals(2, fileInfoList.size());

        System.out.println(fileInfoList);
    }


    @Test
    void findAllTest() {
        fileRepository.insertRoot(catalogId, OffsetDateTime.now());
        Long parentFolderId = fileRepository.insertToRoot(catalogId, "Folder", OffsetDateTime.now());

        Long folderId = fileRepository.insertToFolder(catalogId, parentFolderId, "Folder", OffsetDateTime.now());
        Long fileId = fileRepository.insertToFolder(catalogId, parentFolderId, fileHandle.id(), "File Name", OffsetDateTime.now());

        List<FileInfo> fileInfoList = fileRepository.findAll(catalogId, parentFolderId);
        assertEquals(2, fileInfoList.size());

        System.out.println(fileInfoList);
    }
}

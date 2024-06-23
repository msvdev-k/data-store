package ru.msvdev.ds.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.file.ChunkingSchema;
import ru.msvdev.ds.server.openapi.model.*;
import ru.msvdev.ds.server.property.UploadSessionProperty;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FileControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog";
    private static final String UPLOAD_FILE_URL = "/catalog/%d/file";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final MockMvc mockMvc;
    private final UploadSessionProperty property;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HexFormat hexFormat = HexFormat.of().withLowerCase();
    private final Random random = new Random();

    private UUID userID;
    private Long catalogId;

    private byte[] content;
    private ChunkingSchema schema;
    private String sha256;


    @Autowired
    public FileControllerTest(MockMvc mockMvc, UploadSessionProperty property) {
        this.mockMvc = mockMvc;
        this.property = property;

        objectMapper.registerModule(new JavaTimeModule());
    }


    @BeforeEach
    public void beforeEach() throws Exception {
        userID = UUID.randomUUID();
        catalogId = insertCatalog();

        long contentSize = 2984;

        schema = ChunkingSchema.of(contentSize, (int) property.chunkSize().toBytes(), (int) property.minChunkSize().toBytes());
        content = new byte[(int) contentSize];
        random.nextBytes(content);
        sha256 = hexFormat.formatHex(MessageDigest.getInstance("SHA-256").digest(content));
    }

    @AfterEach
    public void afterEach() throws Exception {
        deleteCatalog(catalogId);
    }


    @Test
    void openUploadSessionTest() throws Exception {
        UploadFileResponse response = openUploadSessionRequest(new UploadFileRequest(sha256, schema.size()));

        assertEquals(UploadFileState.UPLOAD, response.getState());
        assertEquals(sha256, response.getSha256());
        assertEquals(schema.size(), response.getSize());
        assertEquals(schema.count(), response.getChunkCount());
        assertEquals(schema.chunkSize(), response.getChunkSize());
        assertEquals(schema.lastChunkSize(), response.getLastChunkSize());
        assertNotNull(response.getUploadSession());
        assertEquals(ChunkingSchema.FIRST_CHUNK_NUMBER, response.getUploadNumber());
        assertEquals(schema.getOffsetChunk(response.getUploadNumber()), response.getUploadOffset());
        assertEquals(schema.getChunkSize(response.getUploadNumber()), response.getUploadSize());
        assertTrue(response.getUploadEnd().isAfter(OffsetDateTime.now()));
    }


    @Test
    void uploadChunkSessionTest() throws Exception {
        UploadFileResponse response1 = openUploadSessionRequest(new UploadFileRequest(sha256, schema.size()));

        // Диапазон данных для отправки на сервер
        int from = Math.toIntExact(response1.getUploadOffset());
        int to = from + response1.getUploadSize();

        UploadChunkRequest uploadChunkRequest = new UploadChunkRequest(
                response1.getUploadSession(),
                response1.getUploadNumber(),
                Base64.getEncoder().encodeToString(Arrays.copyOfRange(content, from, to))
        );

        UploadFileResponse response2 = uploadChunkSessionRequest(uploadChunkRequest);


        assertEquals(UploadFileState.UPLOAD, response2.getState());
        assertEquals(sha256, response2.getSha256());
        assertEquals(schema.size(), response2.getSize());
        assertEquals(schema.count(), response2.getChunkCount());
        assertEquals(schema.chunkSize(), response2.getChunkSize());
        assertEquals(schema.lastChunkSize(), response2.getLastChunkSize());
        assertEquals(response1.getUploadSession(), response2.getUploadSession());
        assertEquals(ChunkingSchema.FIRST_CHUNK_NUMBER + 1, response2.getUploadNumber());
        assertEquals(schema.getOffsetChunk(response2.getUploadNumber()), response2.getUploadOffset());
        assertEquals(schema.getChunkSize(response2.getUploadNumber()), response2.getUploadSize());
        assertTrue(response2.getUploadEnd().isAfter(OffsetDateTime.now()));
    }


    @Test
    void uploadContentAlgorithmTest() throws Exception {

        // Открыть сессию выгрузки файла
        UploadFileResponse response = openUploadSessionRequest(new UploadFileRequest(sha256, schema.size()));

        {
            assertEquals(UploadFileState.UPLOAD, response.getState());
            assertEquals(sha256, response.getSha256());
            assertEquals(schema.size(), response.getSize());
            assertEquals(schema.count(), response.getChunkCount());
            assertEquals(schema.chunkSize(), response.getChunkSize());
            assertEquals(schema.lastChunkSize(), response.getLastChunkSize());
            assertNotNull(response.getUploadSession());
            assertEquals(ChunkingSchema.FIRST_CHUNK_NUMBER, response.getUploadNumber());
            assertEquals(schema.getOffsetChunk(response.getUploadNumber()), response.getUploadOffset());
            assertEquals(schema.getChunkSize(response.getUploadNumber()), response.getUploadSize());
            assertTrue(response.getUploadEnd().isAfter(OffsetDateTime.now()));
        }


        // Цикл для выгрузки фрагментов на сервер
        while (response.getState() == UploadFileState.UPLOAD) {
            {
                assertEquals(UploadFileState.UPLOAD, response.getState());
                assertEquals(sha256, response.getSha256());
                assertEquals(schema.size(), response.getSize());
                assertEquals(schema.count(), response.getChunkCount());
                assertEquals(schema.chunkSize(), response.getChunkSize());
                assertEquals(schema.lastChunkSize(), response.getLastChunkSize());
                assertNotNull(response.getUploadSession());
                assertNotNull(response.getUploadNumber());
                assertEquals(schema.getOffsetChunk(response.getUploadNumber()), response.getUploadOffset());
                assertEquals(schema.getChunkSize(response.getUploadNumber()), response.getUploadSize());
                assertTrue(response.getUploadEnd().isAfter(OffsetDateTime.now()));
            }

            // Диапазон данных для отправки на сервер
            int from = Math.toIntExact(response.getUploadOffset());
            int to = from + response.getUploadSize();

            // Параметры запроса
            UploadChunkRequest uploadChunkRequest = new UploadChunkRequest(
                    response.getUploadSession(),
                    response.getUploadNumber(),
                    Base64.getEncoder().encodeToString(Arrays.copyOfRange(content, from, to))
            );

            // Выгрузить фрагмент файла на сервер
            response = uploadChunkSessionRequest(uploadChunkRequest);
        }

        {
            assertEquals(UploadFileState.PROCESSING, response.getState());
            assertEquals(sha256, response.getSha256());
            assertEquals(schema.size(), response.getSize());
            assertEquals(schema.count(), response.getChunkCount());
            assertEquals(schema.chunkSize(), response.getChunkSize());
            assertEquals(schema.lastChunkSize(), response.getLastChunkSize());
            assertNotNull(response.getUploadSession());
            assertNull(response.getUploadNumber());
            assertNull(response.getUploadOffset());
            assertNull(response.getUploadSize());
            assertNull(response.getUploadEnd());
        }

        // Цикл ожидания проверки выгруженных фрагментов
        int maxCount = 200;
        UploadFileRequest uploadFileRequest = new UploadFileRequest(sha256, schema.size());

        for (int i = 0; i < maxCount; i++) {
            response = openUploadSessionRequest(uploadFileRequest);

            if (response.getState() == UploadFileState.ARCHIVE) {
                break;

            } else if (response.getState() == UploadFileState.PROCESSING) {
                Thread.sleep(1000);

            } else {
                throw new RuntimeException();
            }
        }

        {
            assertEquals(UploadFileState.ARCHIVE, response.getState());
            assertEquals(sha256, response.getSha256());
            assertEquals(schema.size(), response.getSize());
            assertEquals(schema.count(), response.getChunkCount());
            assertEquals(schema.chunkSize(), response.getChunkSize());
            assertEquals(schema.lastChunkSize(), response.getLastChunkSize());
            assertNull(response.getUploadSession());
            assertNull(response.getUploadNumber());
            assertNull(response.getUploadOffset());
            assertNull(response.getUploadSize());
            assertNull(response.getUploadEnd());
        }
    }


    private UploadFileResponse openUploadSessionRequest(UploadFileRequest uploadFileRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post(String.format(UPLOAD_FILE_URL, catalogId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadFileRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), UploadFileResponse.class);
    }


    private UploadFileResponse uploadChunkSessionRequest(UploadChunkRequest uploadChunkRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put(String.format(UPLOAD_FILE_URL, catalogId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadChunkRequest))
                )
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), UploadFileResponse.class);
    }


    private Long insertCatalog() throws Exception {
        CatalogRequest catalogRequest = new CatalogRequest("Каталог");
        MockHttpServletResponse response = mockMvc
                .perform(post(BASE_URL)
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catalogRequest))
                )
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CatalogResponse catalogResponse = objectMapper.readValue(response.getContentAsString(), CatalogResponse.class);
        assertNotNull(catalogResponse.getId());

        return catalogResponse.getId();
    }

    private void deleteCatalog(long catalogId) throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + catalogId).header(USER_UUID_HEADER, userID))
//                .andDo(print())
                .andExpect(status().isNoContent());
    }

}

package ru.msvdev.ds.server.module.upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.upload.base.ChunkingSchema;
import ru.msvdev.ds.server.openapi.model.UploadChunkRequest;
import ru.msvdev.ds.server.openapi.model.UploadRequest;
import ru.msvdev.ds.server.openapi.model.UploadResponse;
import ru.msvdev.ds.server.openapi.model.UploadState;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
        value = {"classpath:module/upload/upload-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class UploadControllerTest extends ApplicationTest {

    private static final String UPLOAD_URL = "/catalog/%d/upload";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";


    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static String userID;
    private static long catalogId;
    private static long uploadSessionId;

    private static int chunkSize;
    private static int minChunkSize;


    @Autowired
    public UploadControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        objectMapper.registerModule(new JavaTimeModule());
    }


    @BeforeAll
    public static void beforeEach() {
        userID = "cf13dd5d-0092-47c7-93dd-5d0092f7c7d0";
        catalogId = 101;
        uploadSessionId = 237;

        chunkSize = 1024;
        minChunkSize = 512;
    }


    @ParameterizedTest
    @MethodSource
    void openSession(UploadRequest request, UploadResponse expectedResponse) throws Exception {
        // region Given
        String url = String.format(UPLOAD_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .header(USER_UUID_HEADER, userID)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andReturn().getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());

        UploadResponse actualResponse = objectMapper.readValue(response.getContentAsString(), UploadResponse.class);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getState(), actualResponse.getState());
        assertEquals(expectedResponse.getSha256(), actualResponse.getSha256());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertEquals(expectedResponse.getChunkCount(), actualResponse.getChunkCount());
        assertEquals(expectedResponse.getChunkSize(), actualResponse.getChunkSize());
        assertEquals(expectedResponse.getLastChunkSize(), actualResponse.getLastChunkSize());
        assertEquals(expectedResponse.getUploadSession(), actualResponse.getUploadSession());
        assertEquals(expectedResponse.getChunkNumber(), actualResponse.getChunkNumber());
        assertEquals(expectedResponse.getUploadOffset(), actualResponse.getUploadOffset());
        assertEquals(expectedResponse.getUploadSize(), actualResponse.getUploadSize());

        if (expectedResponse.getUploadEnd() != null) {
            assertTrue(actualResponse.getUploadEnd().isAfter(expectedResponse.getUploadEnd()));
        } else {
            assertNull(actualResponse.getUploadEnd());
        }
        // endregion
    }

    private static Stream<Arguments> openSession() {
        return Stream.of(
                openSession_newSession(),
                openSession_archiveContainer(),
                openSession_117_uploadSession(),
                openSession_127_uploadProcessingSession(),
                openSession_137_processingSession()
        );
    }

    private static Arguments openSession_newSession() {
        String sha256 = "e2d8ccd182fa8c27ce97ecbe87a774a786f8a455ceb843f6dc76ec2b2d459bf5";
        ChunkingSchema schema = ChunkingSchema.of(5496247, chunkSize, minChunkSize);

        UploadRequest request = new UploadRequest(sha256, schema.size());
        UploadResponse response = new UploadResponse()
                .state(UploadState.UPLOAD)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(uploadSessionId)
                .chunkNumber(ChunkingSchema.FIRST_CHUNK_NUMBER)
                .uploadOffset(schema.getOffsetChunk(ChunkingSchema.FIRST_CHUNK_NUMBER))
                .uploadSize(schema.getChunkSize(ChunkingSchema.FIRST_CHUNK_NUMBER))
                .uploadEnd(OffsetDateTime.now());

        return Arguments.of(request, response);
    }

    private static Arguments openSession_archiveContainer() {
        String sha256 = "4a344710a377f16de8053f6b273c9fbe9f434301f1f11fd79998c6930e9ccb63";
        ChunkingSchema schema = new ChunkingSchema(134, 4, 32, 38);

        UploadRequest request = new UploadRequest(sha256, schema.size());
        UploadResponse response = new UploadResponse()
                .state(UploadState.ARCHIVE)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(null)
                .chunkNumber(null)
                .uploadOffset(null)
                .uploadSize(null)
                .uploadEnd(null);

        return Arguments.of(request, response);
    }

    private static Arguments openSession_117_uploadSession() {
        String sha256 = "0c6c5f1cb1f143780f27ee0e79d977f527fb1b47b6b58f8111efc3e6510916cf";
        ChunkingSchema schema = new ChunkingSchema(184, 6, 32, 24);

        UploadRequest request = new UploadRequest(sha256, schema.size());
        UploadResponse response = new UploadResponse()
                .state(UploadState.UPLOAD)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(117L)
                .chunkNumber(3)
                .uploadOffset(schema.getOffsetChunk(3))
                .uploadSize(schema.getChunkSize(3))
                .uploadEnd(OffsetDateTime.now());

        return Arguments.of(request, response);
    }

    private static Arguments openSession_127_uploadProcessingSession() {
        String sha256 = "c55323d4b347ae601b5c507856c7a520a932abe2070c40fa35aeeb8e62cfdb1c";
        ChunkingSchema schema = new ChunkingSchema(114, 4, 32, 18);

        UploadRequest request = new UploadRequest(sha256, schema.size());
        UploadResponse response = new UploadResponse()
                .state(UploadState.PROCESSING)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(127L)
                .chunkNumber(null)
                .uploadOffset(null)
                .uploadSize(null)
                .uploadEnd(null);

        return Arguments.of(request, response);
    }

    private static Arguments openSession_137_processingSession() {
        String sha256 = "0c535d44777d76e3d463d268293c5a9763b1c1336cb52418642dc5d368dfaf60";
        ChunkingSchema schema = new ChunkingSchema(204, 6, 32, 44);

        UploadRequest request = new UploadRequest(sha256, schema.size());
        UploadResponse response = new UploadResponse()
                .state(UploadState.PROCESSING)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(137L)
                .chunkNumber(null)
                .uploadOffset(null)
                .uploadSize(null)
                .uploadEnd(null);

        return Arguments.of(request, response);
    }


    @ParameterizedTest
    @MethodSource
    void addChunk(UploadChunkRequest request, UploadResponse expectedResponse) throws Exception {
        // region Given
        String url = String.format(UPLOAD_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, userID)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andReturn().getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());

        UploadResponse actualResponse = objectMapper.readValue(response.getContentAsString(), UploadResponse.class);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getState(), actualResponse.getState());
        assertEquals(expectedResponse.getSha256(), actualResponse.getSha256());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertEquals(expectedResponse.getChunkCount(), actualResponse.getChunkCount());
        assertEquals(expectedResponse.getChunkSize(), actualResponse.getChunkSize());
        assertEquals(expectedResponse.getLastChunkSize(), actualResponse.getLastChunkSize());
        assertEquals(expectedResponse.getUploadSession(), actualResponse.getUploadSession());
        assertEquals(expectedResponse.getChunkNumber(), actualResponse.getChunkNumber());
        assertEquals(expectedResponse.getUploadOffset(), actualResponse.getUploadOffset());
        assertEquals(expectedResponse.getUploadSize(), actualResponse.getUploadSize());

        if (expectedResponse.getUploadEnd() != null) {
            assertTrue(actualResponse.getUploadEnd().isAfter(expectedResponse.getUploadEnd()));
        } else {
            assertNull(actualResponse.getUploadEnd());
        }
        // endregion
    }

    private static Stream<Arguments> addChunk() {
        return Stream.of(
                addChunk_Session_117(),
                addChunk_Session_147()
        );
    }

    private static Arguments addChunk_Session_117() {
        String sha256 = "0c6c5f1cb1f143780f27ee0e79d977f527fb1b47b6b58f8111efc3e6510916cf";
        ChunkingSchema schema = new ChunkingSchema(184, 6, 32, 24);

        UploadChunkRequest request = new UploadChunkRequest(
                117L, 4, "nSGpe7e1ShI0q+FoJNb9WlpZDj9RHu/n95PRTXbreew="
        );

        UploadResponse response = new UploadResponse()
                .state(UploadState.UPLOAD)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(117L)
                .chunkNumber(3)
                .uploadOffset(schema.getOffsetChunk(3))
                .uploadSize(schema.getChunkSize(3))
                .uploadEnd(OffsetDateTime.now());

        return Arguments.of(request, response);
    }

    private static Arguments addChunk_Session_147() {
        String sha256 = "1cd0b036f07338bfa53cfad34e2d3f904a5c481cf6c26e5991ecebecc04416ec";
        ChunkingSchema schema = new ChunkingSchema(132, 4, 32, 36);

        UploadChunkRequest request = new UploadChunkRequest(
                147L, 4, "chdEsdmhiVVO70AC1Ukr49cLTUoDKAQLGWOIOJXUAXFrVvJe"
        );

        UploadResponse response = new UploadResponse()
                .state(UploadState.PROCESSING)
                .sha256(sha256)
                .size(schema.size())
                .chunkCount(schema.count())
                .chunkSize(schema.chunkSize())
                .lastChunkSize(schema.lastChunkSize())
                .uploadSession(147L)
                .chunkNumber(null)
                .uploadOffset(null)
                .uploadSize(null)
                .uploadEnd(null);

        return Arguments.of(request, response);
    }

}

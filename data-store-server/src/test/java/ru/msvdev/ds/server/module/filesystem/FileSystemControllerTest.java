package ru.msvdev.ds.server.module.filesystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import ru.msvdev.ds.server.module.filesystem.entity.FileInfo;
import ru.msvdev.ds.server.openapi.model.*;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
        value = {"classpath:module/filesystem/file-system-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class FileSystemControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog/%d/fs/%d";
    private static final String DOWNLOAD_URL = "/catalog/%d/fs/%d/download";
    private static final String DOWNLOAD_QUERY_PARAMETER = "chunk";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";


    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private static String masterUuid;
    private static long catalogId;
    private static OffsetDateTime offsetDateTimeForTest;


    @Autowired
    public FileSystemControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        objectMapper.registerModule(new JavaTimeModule());
    }


    @BeforeAll
    static void setUp() {
        masterUuid = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41275";
        catalogId = 112L;
        offsetDateTimeForTest = OffsetDateTime.of(
                2024, 7, 12, 17, 23, 11, 0, ZoneOffset.UTC
        );
    }


    @ParameterizedTest
    @MethodSource
    void getFile(long parentFolderId, Set<FileSystemResponse> expectedResponseSet) throws Exception {
        // region Given
        String url = String.format(BASE_URL, catalogId, parentFolderId);
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, masterUuid);
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
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        List<FileSystemResponse> responses = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedResponseSet, new HashSet<>(responses));
        // endregion
    }

    private static Stream<Arguments> getFile() {
        return Stream.of(
                Arguments.of(0L, Set.of(
                        new FileSystemResponse().nodeId(50L).name("Директория 1").mimeType("inode/directory").size(0L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(51L).name("Директория 2").mimeType("inode/directory").size(0L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(52L).name("Файл 1").mimeType("application/pdf").size(131L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(53L).name("Файл 2").mimeType("application/xml").size(132L).created(offsetDateTimeForTest)
                )),
                Arguments.of(51L, Set.of(
                        new FileSystemResponse().nodeId(54L).name("Директория 3").mimeType("inode/directory").size(0L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(55L).name("Директория 4").mimeType("inode/directory").size(0L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(56L).name("Файл 3").mimeType("application/pdf").size(133L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(57L).name("Файл 4").mimeType("application/zip").size(134L).created(offsetDateTimeForTest),
                        new FileSystemResponse().nodeId(58L).name("Файл 5").mimeType("application/xml").size(135L).created(offsetDateTimeForTest)
                ))
        );
    }


    @ParameterizedTest
    @MethodSource
    void newFile(long parentFolderId, FileSystemRequest request, FileSystemResponse expectedResponse) throws Exception {
        // region Given
        String url = String.format(BASE_URL, catalogId, parentFolderId);
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .header(USER_UUID_HEADER, masterUuid)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andReturn().getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());

        FileSystemResponse actualResponse = objectMapper.readValue(response.getContentAsString(), FileSystemResponse.class);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getNodeId(), actualResponse.getNodeId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getMimeType(), actualResponse.getMimeType());
        assertEquals(expectedResponse.getSize(), actualResponse.getSize());
        assertTrue(actualResponse.getCreated().isAfter(expectedResponse.getCreated()));
        // endregion
    }

    private static Stream<Arguments> newFile() {
        return Stream.of(
                Arguments.of(
                        0L,
                        new FileSystemRequest("Название каталога"),
                        new FileSystemResponse().nodeId(157L).name("Название каталога").mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(OffsetDateTime.now())
                ),
                Arguments.of(
                        50L,
                        new FileSystemRequest("Название каталога"),
                        new FileSystemResponse().nodeId(157L).name("Название каталога").mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(OffsetDateTime.now())
                ),
                Arguments.of(
                        54L,
                        new FileSystemRequest("Название каталога"),
                        new FileSystemResponse().nodeId(157L).name("Название каталога").mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(OffsetDateTime.now())
                ),
                Arguments.of(
                        0L,
                        new FileSystemRequest("Название файла").mimeType("application/pdf").sha256("0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990"),
                        new FileSystemResponse().nodeId(157L).name("Название файла").mimeType("application/pdf").size(136L).created(OffsetDateTime.now())
                ),
                Arguments.of(
                        51L,
                        new FileSystemRequest("Название файла").mimeType("application/pdf").sha256("0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990"),
                        new FileSystemResponse().nodeId(157L).name("Название файла").mimeType("application/pdf").size(136L).created(OffsetDateTime.now())
                ),
                Arguments.of(
                        55L,
                        new FileSystemRequest("Название файла").mimeType("application/pdf").sha256("0044256387234782346293409228035c31f78d9846b79a089142a3d4a9331990"),
                        new FileSystemResponse().nodeId(157L).name("Название файла").mimeType("application/pdf").size(136L).created(OffsetDateTime.now())
                )
        );
    }


    @ParameterizedTest
    @MethodSource
    void renameFile(long nodeId, RenameFileRequest request, FileSystemResponse expectedResponse) throws Exception {
        // region Given
        String url = String.format(BASE_URL, catalogId, nodeId);
        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, masterUuid)
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

        FileSystemResponse actualResponse = objectMapper.readValue(response.getContentAsString(), FileSystemResponse.class);

        assertEquals(expectedResponse, actualResponse);
        // endregion
    }

    private static Stream<Arguments> renameFile() {
        String newName = "Новое название файлового объекта";
        return Stream.of(
                Arguments.of(50L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(50L).name(newName).mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(offsetDateTimeForTest)
                ),
                Arguments.of(51L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(51L).name(newName).mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(offsetDateTimeForTest)
                ),
                Arguments.of(52L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(52L).name(newName).mimeType("application/pdf").size(131L).created(offsetDateTimeForTest)
                ),
                Arguments.of(53L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(53L).name(newName).mimeType("application/xml").size(132L).created(offsetDateTimeForTest)
                ),
                Arguments.of(54L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(54L).name(newName).mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(offsetDateTimeForTest)
                ),
                Arguments.of(55L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(55L).name(newName).mimeType(FileInfo.DIRECTORY_MIME_TYPE).size(0L).created(offsetDateTimeForTest)
                ),
                Arguments.of(56L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(56L).name(newName).mimeType("application/pdf").size(133L).created(offsetDateTimeForTest)
                ),
                Arguments.of(57L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(57L).name(newName).mimeType("application/zip").size(134L).created(offsetDateTimeForTest)
                ),
                Arguments.of(58L, new RenameFileRequest(newName),
                        new FileSystemResponse().nodeId(58L).name(newName).mimeType("application/xml").size(135L).created(offsetDateTimeForTest)
                )
        );
    }


    @ParameterizedTest
    @ValueSource(longs = {50, 51, 52, 53, 54, 55, 56, 57, 58})
    void removeFile(long nodeId) throws Exception {
        // region Given
        String url = String.format(BASE_URL, catalogId, nodeId);
        MockHttpServletRequestBuilder requestBuilder = delete(url)
                .header(USER_UUID_HEADER, masterUuid);
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isNoContent());
        // endregion
    }


    @ParameterizedTest
    @MethodSource
    void getFileContent(long nodeId, int chunkNumber, DownloadResponse expectedResponse) throws Exception {
        // region Given
        String url = String.format(DOWNLOAD_URL, catalogId, nodeId);
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, masterUuid);
        if (chunkNumber != 0) requestBuilder.queryParam(DOWNLOAD_QUERY_PARAMETER, String.valueOf(chunkNumber));
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
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        DownloadResponse actualResponse = objectMapper.readValue(response.getContentAsString(), DownloadResponse.class);

        assertEquals(expectedResponse, actualResponse);
        // endregion
    }

    private static Stream<Arguments> getFileContent() {
        return Stream.of(
                Arguments.of(52L, 0, new DownloadResponse()
                        .nodeId(52L).sha256("cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e")
                        .size(131L).chunkCount(4).chunkSize(32).lastChunkSize(35)
                        .number(1).offset(0L).contentSize(32)
                        .content("TgPpdFgRfMz3Y3HBiEABkDxbrCacEt/SiCw2/MRgnTg=")
                ),
                Arguments.of(52L, 1, new DownloadResponse()
                        .nodeId(52L).sha256("cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e")
                        .size(131L).chunkCount(4).chunkSize(32).lastChunkSize(35)
                        .number(1).offset(0L).contentSize(32)
                        .content("TgPpdFgRfMz3Y3HBiEABkDxbrCacEt/SiCw2/MRgnTg=")
                ),
                Arguments.of(52L, 2, new DownloadResponse()
                        .nodeId(52L).sha256("cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e")
                        .size(131L).chunkCount(4).chunkSize(32).lastChunkSize(35)
                        .number(2).offset(32L).contentSize(32)
                        .content("G5Ko57hBafL155CEF+i49xPRcNmwjl3uNYUyxK4JJI0=")
                ),
                Arguments.of(52L, 3, new DownloadResponse()
                        .nodeId(52L).sha256("cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e")
                        .size(131L).chunkCount(4).chunkSize(32).lastChunkSize(35)
                        .number(3).offset(64L).contentSize(32)
                        .content("hEUCTv8+SFLS4IlGe4VR0EpEYMWGCdkvy+Ou4EunYx8=")
                ),
                Arguments.of(52L, 4, new DownloadResponse()
                        .nodeId(52L).sha256("cd0143a6b8d608e7fd482d7813423570c4936e88e68364e5d3f1b745e10be15e")
                        .size(131L).chunkCount(4).chunkSize(32).lastChunkSize(35)
                        .number(4).offset(96L).contentSize(35)
                        .content("Qd24YcF9ruS7LKbjF6MhtQujQOc7KhShOqfTXJni9VUQq5w=")
                )
        );
    }

}

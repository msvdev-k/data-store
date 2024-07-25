package ru.msvdev.ds.server.module.catalog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.CatalogRequest;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
        value = {"classpath:module/catalog/catalog-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class CatalogControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;


    @Autowired
    public CatalogControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @ParameterizedTest
    @MethodSource
    void catalogList(String userUUID, List<CatalogResponse> expectedResponse) throws Exception {
        // region Given
        MockHttpServletRequestBuilder requestBuilder = get(BASE_URL).header(USER_UUID_HEADER, userUUID);
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse servletResponse = resultActions.andReturn().getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        List<CatalogResponse> response = objectMapper.readValue(servletResponse.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedResponse.size(), response.size());
        for (CatalogResponse catalogResponse : expectedResponse) {
            assertTrue(response.contains(catalogResponse));
        }
        // endregion
    }

    private static Stream<Arguments> catalogList() {
        return Stream.of(
                Arguments.of("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272", List.of(
                                new CatalogResponse().id(1L).name("Каталог 01").description("Описание каталога 01").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(2L).name("Каталог 02").description("Описание каталога 02").addAuthoritiesItem(CatalogAuthority.GRANT_AUTHORITY),
                                new CatalogResponse().id(3L).name("Каталог 03").description("Описание каталога 03").addAuthoritiesItem(CatalogAuthority.READING),
                                new CatalogResponse().id(4L).name("Каталог 04").description("Описание каталога 04").addAuthoritiesItem(CatalogAuthority.WRITING),
                                new CatalogResponse().id(5L).name("Каталог 05").description("Описание каталога 05").addAuthoritiesItem(CatalogAuthority.DELETING),
                                new CatalogResponse().id(6L).name("Каталог 06").description("Описание каталога 06").addAuthoritiesItem(CatalogAuthority.FIELD_TEMPLATE_WRITING),
                                new CatalogResponse().id(7L).name("Каталог 07").description("Описание каталога 07").addAuthoritiesItem(CatalogAuthority.FIELD_TEMPLATE_DELETING),
                                new CatalogResponse().id(8L).name("Каталог 08").description("Описание каталога 08").addAuthoritiesItem(CatalogAuthority.FILE_UPLOAD),
                                new CatalogResponse().id(9L).name("Каталог 09").description("Описание каталога 09").addAuthoritiesItem(CatalogAuthority.FILE_DOWNLOAD),
                                new CatalogResponse().id(10L).name("Каталог 10").description("Описание каталога 10").addAuthoritiesItem(CatalogAuthority.FILE_SYSTEM_READ),
                                new CatalogResponse().id(11L).name("Каталог 11").description("Описание каталога 11").addAuthoritiesItem(CatalogAuthority.FILE_SYSTEM_WRITE),
                                new CatalogResponse().id(12L).name("Каталог 12").description("Описание каталога 12").addAuthoritiesItem(CatalogAuthority.FILE_SYSTEM_DELETE),
                                new CatalogResponse().id(13L).name("Каталог 13").description("Описание каталога 13").addAuthoritiesItem(CatalogAuthority.MASTER)
                        )
                ),
                Arguments.of("90832d42-c151-4da1-832d-42c151ada1f8", List.of(
                                new CatalogResponse().id(14L).name("Каталог 14").description("Описание каталога 14").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(15L).name("Каталог 15").description("Описание каталога 15").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(16L).name("Каталог 16").description("Описание каталога 16").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(17L).name("Каталог 17").description("Описание каталога 17").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(18L).name("Каталог 18").description("Описание каталога 18").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(19L).name("Каталог 19").description("Описание каталога 19").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(20L).name("Каталог 20").description("Описание каталога 20").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(21L).name("Каталог 21").description("Описание каталога 21").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(22L).name("Каталог 22").description("Описание каталога 22").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(23L).name("Каталог 23").description("Описание каталога 23").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(24L).name("Каталог 24").description("Описание каталога 24").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(25L).name("Каталог 25").description("Описание каталога 25").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(26L).name("Каталог 26").description("Описание каталога 26").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(27L).name("Каталог 27").description("Описание каталога 27").addAuthoritiesItem(CatalogAuthority.MASTER),
                                new CatalogResponse().id(28L).name("Каталог 28").description("Описание каталога 28").addAuthoritiesItem(CatalogAuthority.MASTER)
                        )
                )
        );
    }


    @Test
    void addCatalog() throws Exception {
        // region Given
        String userUuid = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
        CatalogRequest catalogRequest = new CatalogRequest("Новый каталог");

        MockHttpServletRequestBuilder requestBuilder = post(BASE_URL)
                .header(USER_UUID_HEADER, userUuid)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catalogRequest));

        long expectedCatalogId = 57;
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse servletResponse = resultActions.andReturn().getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CatalogResponse response = objectMapper.readValue(servletResponse.getContentAsString(), CatalogResponse.class);

        assertEquals(expectedCatalogId, response.getId());
        assertEquals(catalogRequest.getName(), response.getName());
        assertNull(response.getDescription());
        assertEquals(1, response.getAuthorities().size());
        assertEquals(CatalogAuthority.MASTER, response.getAuthorities().get(0));
        // endregion
    }


    @Test
    void updateCatalogById() throws Exception {
        // region Given
        long catalogId = 1;
        String url = BASE_URL + "/" + catalogId;
        String userUuid = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
        CatalogRequest catalogRequest = new CatalogRequest("Новый название каталог").description("Новое описание каталога");

        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, userUuid)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(catalogRequest));

        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse servletResponse = resultActions.andReturn().getResponse();
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CatalogResponse response = objectMapper.readValue(servletResponse.getContentAsString(), CatalogResponse.class);

        assertEquals(catalogId, response.getId());
        assertEquals(catalogRequest.getName(), response.getName());
        assertEquals(catalogRequest.getDescription(), response.getDescription());
        assertEquals(1, response.getAuthorities().size());
        assertEquals(CatalogAuthority.MASTER, response.getAuthorities().get(0));
        // endregion
    }


    @Test
    void removeCatalogById() throws Exception {
        // region Given
        long catalogId = 1;
        String url = BASE_URL + "/" + catalogId;
        String userUuid = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";

        MockHttpServletRequestBuilder requestBuilder = delete(url).header(USER_UUID_HEADER, userUuid);
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

}

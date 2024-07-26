package ru.msvdev.ds.server.module.field;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
import ru.msvdev.ds.server.openapi.model.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
        value = {"classpath:module/field/field-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class FieldControllerTest extends ApplicationTest {

    private static final String URL_TEMPLATE = "/catalog/%d/field";
    private static final String URL_FIELD_ID_TEMPLATE = "/catalog/%d/field/%d";


    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private String masterUserId;
    private long catalogId;


    @Autowired
    public FieldControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() {
        catalogId = 1;
        masterUserId = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
    }


    @Test
    void fieldList() throws Exception {
        // region Given
        String url = String.format(URL_TEMPLATE, catalogId);
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, masterUserId);

        List<FieldResponse> expectedResponse = List.of(
                new FieldResponse().id(11L).order(1).name("null").description(null).type(FieldTypes.NULL),
                new FieldResponse().id(12L).order(2).name("integer").description("Long").type(FieldTypes.INTEGER),
                new FieldResponse().id(13L).order(3).name("double").description("Double").type(FieldTypes.DOUBLE),
                new FieldResponse().id(14L).order(4).name("big_decimal").description("BigDecimal").type(FieldTypes.BIG_DECIMAL),
                new FieldResponse().id(15L).order(5).name("string").description("String").type(FieldTypes.STRING),
                new FieldResponse().id(16L).order(6).name("text").description("String").type(FieldTypes.TEXT),
                new FieldResponse().id(17L).order(7).name("date").description("LocalDate").type(FieldTypes.DATE),
                new FieldResponse().id(18L).order(8).name("datetime").description("OffsetDateTime").type(FieldTypes.DATETIME),
                new FieldResponse().id(19L).order(9).name("boolean").description("Boolean").type(FieldTypes.BOOLEAN),
                new FieldResponse().id(20L).order(10).name("bytes").description("String").type(FieldTypes.BYTES),
                new FieldResponse().id(21L).order(11).name("uuid").description("UUID").type(FieldTypes.UUID),
                new FieldResponse().id(22L).order(12).name("json").description("String").type(FieldTypes.JSON),
                new FieldResponse().id(23L).order(13).name("file_id").description(null).type(FieldTypes.FILE_ID)
        );
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

        List<FieldResponse> response = objectMapper.readValue(servletResponse.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedResponse.size(), response.size());
        for (FieldResponse fieldResponse : expectedResponse) {
            assertTrue(response.contains(fieldResponse));
        }
        // endregion
    }


    @Test
    void addField() throws Exception {
        // region Given
        long expectedFieldId = 37;
        FieldRequest fieldRequest = new FieldRequest("Год издания", FieldTypes.INTEGER)
                .order(18).description("Описание").format("формат");


        String url = String.format(URL_TEMPLATE, catalogId);
        MockHttpServletRequestBuilder requestBuilder = post(url)
                .header(USER_UUID_HEADER, masterUserId)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fieldRequest));
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

        FieldResponse response = objectMapper.readValue(servletResponse.getContentAsString(), FieldResponse.class);

        assertNotNull(response);
        assertEquals(expectedFieldId, response.getId());
        assertEquals(fieldRequest.getOrder(), response.getOrder());
        assertEquals(fieldRequest.getName(), response.getName());
        assertEquals(fieldRequest.getDescription(), response.getDescription());
        assertEquals(fieldRequest.getType(), response.getType());
        assertEquals(fieldRequest.getFormat(), response.getFormat());
        // endregion
    }


    @Test
    void updateFieldById() throws Exception {
        // region Given
        long fieldId = 12;
        FieldRequest fieldRequest = new FieldRequest("Год издания", FieldTypes.DOUBLE)
                .order(18).description("Описание").format("формат");


        String url = String.format(URL_FIELD_ID_TEMPLATE, catalogId, fieldId);
        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, masterUserId)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fieldRequest));
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

        FieldResponse response = objectMapper.readValue(servletResponse.getContentAsString(), FieldResponse.class);

        assertNotNull(response);
        assertEquals(fieldId, response.getId());
        assertEquals(fieldRequest.getOrder(), response.getOrder());
        assertEquals(fieldRequest.getName(), response.getName());
        assertEquals(fieldRequest.getDescription(), response.getDescription());
        assertEquals(FieldTypes.INTEGER, response.getType());
        assertNull(response.getFormat());
        // endregion
    }


    @ParameterizedTest
    @ValueSource(longs = {11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23})
    void removeFieldById(long fieldId) throws Exception {
        // region Given
        String url = String.format(URL_FIELD_ID_TEMPLATE, catalogId, fieldId);
        MockHttpServletRequestBuilder requestBuilder = delete(url).header(USER_UUID_HEADER, masterUserId);
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

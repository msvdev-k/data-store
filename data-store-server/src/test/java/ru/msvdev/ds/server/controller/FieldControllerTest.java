package ru.msvdev.ds.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.msvdev.ds.server.openapi.model.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FieldControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog";
    private static final String URL_TEMPLATE = "/catalog/%d/field";
    private static final String URL_FIELD_ID_TEMPLATE = "/catalog/%d/field/%d";


    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private UUID userID;
    private Long catalogId;


    @Autowired
    public FieldControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() throws Exception {
        userID = UUID.randomUUID();

        CatalogRequest catalogRequest = new CatalogRequest("Каталог");
        CatalogResponse catalogResponse = addCatalogRequest(catalogRequest);
        assertNotNull(catalogResponse.getId());
        catalogId = catalogResponse.getId();
    }

    @AfterEach
    public void afterEach() throws Exception {
        deleteCatalogRequest(catalogId);
    }


    @Test
    void crudTest() throws Exception {
        List<FieldRequest> fieldRequests = getFieldRequests();
        FieldResponse[] fieldResponses = new FieldResponse[fieldRequests.size()];


        // = addFieldRequest ==========================
        for (int i = 0; i < fieldRequests.size(); i++) {
            FieldRequest fieldRequest = fieldRequests.get(i);
            FieldResponse fieldResponse = addFieldRequest(fieldRequest);

            fieldResponses[i] = fieldResponse;
            assertRequestResponse(fieldRequest, fieldResponse);
        }


        // = getFieldRequest ==========================
        List<FieldResponse> fieldResponseList = getFieldRequest();
        assertFalse(fieldResponseList.isEmpty());
        for (FieldResponse fieldResponse : fieldResponses) {
            assertTrue(fieldResponseList.contains(fieldResponse));
        }


        // = updateFieldRequest =======================
        int fieldNumber = 4;
        FieldRequest fieldRequest = fieldRequests.get(fieldNumber);
        fieldRequest.setOrder(847356);
        fieldRequest.setName("Новое название поля");
        fieldRequest.setDescription("Новое описание поля");
        FieldResponse response = updateFieldRequest(fieldResponses[fieldNumber].getId(), fieldRequest);
        assertEquals(fieldResponses[fieldNumber].getId(), response.getId());
        assertEquals(fieldRequest.getName(), response.getName());
        assertEquals(fieldRequest.getDescription(), response.getDescription());


        // = deleteFieldRequest =======================
        for (FieldResponse fieldResponse : fieldResponses) {
            deleteFieldRequest(fieldResponse.getId());
        }

        assertTrue(getFieldRequest().isEmpty());
    }


    private List<FieldRequest> getFieldRequests() {
        FieldRequest fieldRequestNULL = new FieldRequest("Поле NULL", FieldTypes.NULL);
        FieldRequest fieldRequestINTEGER = new FieldRequest("Поле INTEGER", FieldTypes.INTEGER);
        FieldRequest fieldRequestDOUBLE = new FieldRequest("Поле DOUBLE", FieldTypes.DOUBLE);
        FieldRequest fieldRequestBIG_DECIMAL = new FieldRequest("Поле BIG_DECIMAL", FieldTypes.BIG_DECIMAL);
        FieldRequest fieldRequestSTRING = new FieldRequest("Поле STRING", FieldTypes.STRING);
        FieldRequest fieldRequestTEXT = new FieldRequest("Поле TEXT", FieldTypes.TEXT);
        FieldRequest fieldRequestDATE = new FieldRequest("Поле DATE", FieldTypes.DATE);
        FieldRequest fieldRequestDATETIME = new FieldRequest("Поле DATETIME", FieldTypes.DATETIME);
        FieldRequest fieldRequestBOOLEAN = new FieldRequest("Поле BOOLEAN", FieldTypes.BOOLEAN);
        FieldRequest fieldRequestBYTES = new FieldRequest("Поле BYTES", FieldTypes.BYTES);
        FieldRequest fieldRequestUUID = new FieldRequest("Поле UUID", FieldTypes.UUID);
        FieldRequest fieldRequestJSON = new FieldRequest("Поле JSON", FieldTypes.JSON);

        return List.of(fieldRequestNULL, fieldRequestINTEGER, fieldRequestDOUBLE,
                fieldRequestBIG_DECIMAL, fieldRequestSTRING, fieldRequestTEXT,
                fieldRequestDATE, fieldRequestDATETIME, fieldRequestBOOLEAN,
                fieldRequestBYTES, fieldRequestUUID, fieldRequestJSON);
    }


    private FieldResponse addFieldRequest(FieldRequest fieldRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post(String.format(URL_TEMPLATE, catalogId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), FieldResponse.class);
    }


    private List<FieldResponse> getFieldRequest() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(String.format(URL_TEMPLATE, catalogId)).header(USER_UUID_HEADER, userID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
    }


    private FieldResponse updateFieldRequest(long fieldId, FieldRequest fieldRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put(String.format(URL_FIELD_ID_TEMPLATE, catalogId, fieldId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), FieldResponse.class);
    }


    private void deleteFieldRequest(long fieldId) throws Exception {
        mockMvc.perform(
                        delete(String.format(URL_FIELD_ID_TEMPLATE, catalogId, fieldId))
                                .header(USER_UUID_HEADER, userID)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    private void assertRequestResponse(FieldRequest fieldRequest, FieldResponse fieldResponse) {
        assertNotNull(fieldResponse.getId());
        assertNotNull(fieldResponse.getName());
        assertNotNull(fieldResponse.getType());

        assertEquals(fieldRequest.getOrder(), fieldResponse.getOrder());
        assertEquals(fieldRequest.getName(), fieldResponse.getName());
        assertEquals(fieldRequest.getDescription(), fieldResponse.getDescription());
        assertEquals(fieldRequest.getType(), fieldResponse.getType());
        assertEquals(fieldRequest.getFormat(), fieldResponse.getFormat());
    }


    private CatalogResponse addCatalogRequest(CatalogRequest catalogRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post(BASE_URL)
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catalogRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), CatalogResponse.class);
    }

    private void deleteCatalogRequest(long catalogId) throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + catalogId).header(USER_UUID_HEADER, userID))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


}

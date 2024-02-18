package ru.msvdev.ds.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.openapi.model.CatalogAuthority;
import ru.msvdev.ds.server.openapi.model.CatalogRequest;
import ru.msvdev.ds.server.openapi.model.CatalogResponse;

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
public class CatalogControllerTest extends ApplicationTest {

    private static final String REQUEST_URL = "/catalog";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private UUID userID;


    @Autowired
    public CatalogControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() {
        userID = UUID.randomUUID();
    }


    @Test
    void crudTest() throws Exception {
        CatalogRequest catalogRequest1 = new CatalogRequest("Первый каталог");
        catalogRequest1.setDescription("Мой первый каталог");
        CatalogRequest catalogRequest2 = new CatalogRequest("Второй каталог");


        // = addCatalogRequest ========================
        CatalogResponse catalogResponse1 = addCatalogRequest(catalogRequest1);
        CatalogResponse catalogResponse2 = addCatalogRequest(catalogRequest2);

        assertRequestResponse(catalogRequest1, catalogResponse1);
        assertRequestResponse(catalogRequest2, catalogResponse2);


        // = getCatalogRequest ========================
        List<CatalogResponse> catalogResponses = getCatalogRequest();
        assertFalse(catalogResponses.isEmpty());
        assertTrue(catalogResponses.contains(catalogResponse1));
        assertTrue(catalogResponses.contains(catalogResponse2));


        // = updateCatalogRequest =====================
        catalogRequest2.setName("Новое название каталога");
        catalogRequest2.setDescription("Новое описание каталога");
        CatalogResponse catalogResponse3 = updateCatalogRequest(catalogResponse2.getId(), catalogRequest2);
        assertEquals(catalogResponse2.getId(), catalogResponse3.getId());
        assertEquals(catalogRequest2.getName(), catalogResponse3.getName());
        assertEquals(catalogRequest2.getDescription(), catalogResponse3.getDescription());
        assertTrue(catalogResponse3.getAuthorities().isEmpty());


        // = deleteCatalogRequest =====================
        deleteCatalogRequest(catalogResponse1.getId());
        deleteCatalogRequest(catalogResponse2.getId());

        assertTrue(getCatalogRequest().isEmpty());
    }


    private List<CatalogResponse> getCatalogRequest() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(REQUEST_URL).header(USER_UUID_HEADER, userID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
    }


    private CatalogResponse addCatalogRequest(CatalogRequest catalogRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post(REQUEST_URL)
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


    private CatalogResponse updateCatalogRequest(long catalogId, CatalogRequest catalogRequest) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put(REQUEST_URL + "/" + catalogId)
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catalogRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), CatalogResponse.class);
    }


    private void deleteCatalogRequest(long catalogId) throws Exception {
        mockMvc.perform(delete(REQUEST_URL + "/" + catalogId).header(USER_UUID_HEADER, userID))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    private void assertRequestResponse(CatalogRequest catalogRequest, CatalogResponse catalogResponse) {
        assertNotNull(catalogResponse.getId());
        assertNotNull(catalogResponse.getName());
        assertEquals(catalogRequest.getName(), catalogResponse.getName());
        assertEquals(catalogRequest.getDescription(), catalogResponse.getDescription());
        assertFalse(catalogResponse.getAuthorities().isEmpty());
        assertTrue(catalogResponse.getAuthorities().contains(CatalogAuthority.MASTER));
    }


}

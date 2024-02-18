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
public class UserControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog";
    private static final String USER_URL = "/catalog/%d/user";


    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String USER_QUERY_PARAMETER = "user";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private UUID masterUser;
    private Long catalogId;
    private UserAuthorities masterUserAuthorities;


    @Autowired
    public UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() throws Exception {
        masterUser = UUID.randomUUID();
        catalogId = insertCatalog();
        masterUserAuthorities = new UserAuthorities(masterUser, List.of(CatalogAuthority.MASTER));
    }

    @AfterEach
    public void afterEach() throws Exception {
        deleteCatalog(catalogId);
    }


    @Test
    void crudTest() throws Exception {
        UUID userUuid = UUID.randomUUID();

        UserAuthorities userAuthorities10 = new UserAuthorities(userUuid, List.of(
                CatalogAuthority.GRANT_AUTHORITY,
                CatalogAuthority.READING,
                CatalogAuthority.WRITING,
                CatalogAuthority.DELETING,
                CatalogAuthority.GRANT_AUTHORITY)
        );
        UserAuthorities userAuthorities11 = new UserAuthorities(userUuid, List.of(
                CatalogAuthority.GRANT_AUTHORITY,
                CatalogAuthority.READING,
                CatalogAuthority.WRITING,
                CatalogAuthority.DELETING)
        );

        UserAuthorities userAuthorities20 = new UserAuthorities(userUuid, List.of(
                CatalogAuthority.MASTER,
                CatalogAuthority.READING,
                CatalogAuthority.FIELD_TEMPLATE_WRITING,
                CatalogAuthority.FIELD_TEMPLATE_DELETING)
        );
        UserAuthorities userAuthorities21 = new UserAuthorities(userUuid, List.of(
                CatalogAuthority.READING,
                CatalogAuthority.FIELD_TEMPLATE_WRITING,
                CatalogAuthority.FIELD_TEMPLATE_DELETING)
        );


        // = getUsersRequest ==========================

        List<UserAuthorities> usersRequest1 = getUsersRequest();

        assertEquals(1, usersRequest1.size());
        assertTrue(usersRequest1.contains(masterUserAuthorities));


        // = updateUsersAuthoritiesRequest1 ===========

        UserAuthorities updatedUsersAuthorities1 = updateUsersAuthoritiesRequest(userAuthorities10);

        assertEquals(userAuthorities11, updatedUsersAuthorities1);

        List<UserAuthorities> usersRequest2 = getUsersRequest();
        assertEquals(2, usersRequest2.size());
        assertTrue(usersRequest2.contains(masterUserAuthorities));
        assertTrue(usersRequest2.contains(userAuthorities11));


        // = updateUsersAuthoritiesRequest2 ===========

        UserAuthorities updatedUsersAuthorities2 = updateUsersAuthoritiesRequest(userAuthorities20);

        assertEquals(userAuthorities21, updatedUsersAuthorities2);

        List<UserAuthorities> usersRequest3 = getUsersRequest();
        assertEquals(2, usersRequest3.size());
        assertTrue(usersRequest3.contains(masterUserAuthorities));
        assertTrue(usersRequest3.contains(userAuthorities21));


        // = deleteUserRequest ========================
        deleteUserRequest(userUuid);

        List<UserAuthorities> usersRequest4 = getUsersRequest();

        assertEquals(1, usersRequest4.size());
        assertTrue(usersRequest4.contains(masterUserAuthorities));
    }


    private List<UserAuthorities> getUsersRequest() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(String.format(USER_URL, catalogId)).header(USER_UUID_HEADER, masterUser))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });
    }


    private UserAuthorities updateUsersAuthoritiesRequest(UserAuthorities userAuthorities) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put(String.format(USER_URL, catalogId))
                        .header(USER_UUID_HEADER, masterUser)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthorities))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), UserAuthorities.class);
    }


    private void deleteUserRequest(UUID userUuid) throws Exception {
        mockMvc.perform(
                        delete(String.format(USER_URL, catalogId))
                                .header(USER_UUID_HEADER, masterUser)
                                .queryParam(USER_QUERY_PARAMETER, userUuid.toString())
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    private Long insertCatalog() throws Exception {
        CatalogRequest catalogRequest = new CatalogRequest("Каталог");
        MockHttpServletResponse response = mockMvc
                .perform(post(BASE_URL)
                        .header(USER_UUID_HEADER, masterUser)
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
        mockMvc.perform(delete(BASE_URL + "/" + catalogId).header(USER_UUID_HEADER, masterUser))
//                .andDo(print())
                .andExpect(status().isNoContent());
    }

}

package ru.msvdev.ds.server.module.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql(
        value = {"classpath:module/user/user-authority-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class UserAuthorityControllerTest extends ApplicationTest {

    private static final String TEMPLATE_URL = "/catalog/%d/user";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String USER_QUERY_PARAMETER = "user";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;


    @Autowired
    public UserAuthorityControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @Test
    void userList() throws Exception {
        // region Given
        long catalogId = 1;
        String masterUser = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";

        String url = String.format(TEMPLATE_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, masterUser);

        List<UserAuthorities> expectedUserAuthorities = List.of(
                new UserAuthorities(
                        UUID.fromString("bfe5e92a-ba1f-4412-a5e9-2aba1fc41272"),
                        List.of(CatalogAuthority.MASTER)
                ),
                new UserAuthorities(
                        UUID.fromString("42b16c80-7987-462b-b16c-807987062be1"),
                        List.of(
                                CatalogAuthority.GRANT_AUTHORITY,
                                CatalogAuthority.READING,
                                CatalogAuthority.WRITING,
                                CatalogAuthority.DELETING,
                                CatalogAuthority.FIELD_TEMPLATE_WRITING,
                                CatalogAuthority.FIELD_TEMPLATE_DELETING,
                                CatalogAuthority.FILE_UPLOAD,
                                CatalogAuthority.FILE_DOWNLOAD,
                                CatalogAuthority.FILE_SYSTEM_READ,
                                CatalogAuthority.FILE_SYSTEM_WRITE,
                                CatalogAuthority.FILE_SYSTEM_DELETE
                        )
                ),
                new UserAuthorities(
                        UUID.fromString("64f25d2f-953f-4605-b25d-2f953f260558"),
                        List.of(CatalogAuthority.READING)
                )
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

        List<UserAuthorities> response = objectMapper.readValue(servletResponse.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUserAuthorities.size(), response.size());
        for (UserAuthorities userAuthorities : expectedUserAuthorities) {
            Optional<UserAuthorities> optionalUserAuthorities = response.stream().filter(a -> a.getUserUuid().equals(userAuthorities.getUserUuid())).findFirst();
            assertTrue(optionalUserAuthorities.isPresent());

            List<CatalogAuthority> expectedAuthorities = userAuthorities.getAuthorities();
            List<CatalogAuthority> actualAuthorities = optionalUserAuthorities.get().getAuthorities();
            assertEquals(expectedAuthorities.size(), actualAuthorities.size());
            for (CatalogAuthority catalogAuthority : expectedAuthorities) {
                assertTrue(actualAuthorities.contains(catalogAuthority));
            }
        }
        // endregion
    }


    @Test
    void updateUserAuthorities() throws Exception {
        // region Given
        long catalogId = 1;
        String masterUser = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";

        UserAuthorities userAuthorities = new UserAuthorities(
                UUID.fromString("64f25d2f-953f-4605-b25d-2f953f260558"),
                List.of(
                        CatalogAuthority.MASTER,
                        CatalogAuthority.FIELD_TEMPLATE_WRITING,
                        CatalogAuthority.FIELD_TEMPLATE_DELETING,
                        CatalogAuthority.FILE_UPLOAD,
                        CatalogAuthority.FILE_DOWNLOAD,
                        CatalogAuthority.FILE_SYSTEM_READ,
                        CatalogAuthority.FILE_SYSTEM_WRITE,
                        CatalogAuthority.FILE_SYSTEM_DELETE
                )
        );

        String url = String.format(TEMPLATE_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, masterUser)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuthorities));
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

        UserAuthorities response = objectMapper.readValue(servletResponse.getContentAsString(), UserAuthorities.class);

        assertNotNull(response);
        assertEquals(userAuthorities.getUserUuid(), response.getUserUuid());

        List<CatalogAuthority> expectedAuthorities = userAuthorities.getAuthorities().stream().filter(a -> a != CatalogAuthority.MASTER).toList();
        List<CatalogAuthority> actualAuthorities = response.getAuthorities();
        assertEquals(expectedAuthorities.size(), actualAuthorities.size());
        for (CatalogAuthority authority : expectedAuthorities) {
            assertTrue(actualAuthorities.contains(authority));
        }
        // endregion
    }


    @Test
    void removeUserAuthorities() throws Exception {
        // region Given
        long catalogId = 1;
        String masterUser = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
        String userUuid = "42b16c80-7987-462b-b16c-807987062be1";

        String url = String.format(TEMPLATE_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = delete(url)
                .header(USER_UUID_HEADER, masterUser)
                .queryParam(USER_QUERY_PARAMETER, userUuid);
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

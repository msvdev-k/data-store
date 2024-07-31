package ru.msvdev.ds.server.module.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import ru.msvdev.ds.server.openapi.model.*;

import java.nio.charset.StandardCharsets;
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
        value = {"classpath:module/tag/tag-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class TagControllerTest extends ApplicationTest {

    private static final String CARD_TAG_URL = "/catalog/%d/card/%d/tag";
    private static final String CARD_TAG_FIELD_ID_URL = "/catalog/%d/card/%d/tag/%d";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private static String userID;
    private static long catalogId;
    private static long cardId;


    @Autowired
    public TagControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeAll
    public static void beforeEach() {
        userID = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
        catalogId = 1;
        cardId = 41;
    }


    @ParameterizedTest
    @MethodSource
    void addTags(CardTag cardTag) throws Exception {
        // region Given
        CardTag nullCardTag = new CardTag().fieldId(21L).value("");
        List<CardTag> cardTags = List.of(
                new CardTag().fieldId(21L).value(""),
                new CardTag().fieldId(22L).value("32452840"),
                new CardTag().fieldId(23L).value("3.14E-9"),
                new CardTag().fieldId(24L).value("32452840.456"),
                new CardTag().fieldId(25L).value("Строковое значение"),
                new CardTag().fieldId(26L).value("Очень длинный текст..."),
                new CardTag().fieldId(27L).value("2024-06-25"),
                new CardTag().fieldId(28L).value("2024-06-25T19:03:57Z"),
                new CardTag().fieldId(29L).value("true"),
                new CardTag().fieldId(30L).value("aXVuZnE5NzHvv70yM240MW4zNCA5MzIxODMgYDgtIDFgMDI="),
                new CardTag().fieldId(31L).value("bb5d11b6-3b10-414d-9d11-b63b10714dd2"),
                new CardTag().fieldId(32L).value("{\"tags\": {\"a\": 1, \"b\": null}, \"figure\": [true, \"square\"]}"),
                new CardTag().fieldId(33L).value("1"),
                cardTag
        );
        int expectedCardTagCount = (cardTag.equals(nullCardTag)) ? cardTags.size() - 1 : cardTags.size();

        String url = String.format(CARD_TAG_URL, catalogId, cardId);
        MockHttpServletRequestBuilder requestBuilder = put(url)
                .header(USER_UUID_HEADER, userID)
                .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cardTags));
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

        CardResponse cardResponse = objectMapper.readValue(response.getContentAsString(), CardResponse.class);
        assertNotNull(cardResponse);
        assertEquals(cardId, cardResponse.getId());

        List<CardTag> actualTags = cardResponse.getTags();

        assertEquals(expectedCardTagCount, actualTags.size());
        assertEquals(Set.copyOf(cardTags), Set.copyOf(actualTags));
        // endregion
    }

    private static Stream<Arguments> addTags() {
        return Stream.of(
                Arguments.of(new CardTag().fieldId(21L).value("")),
                Arguments.of(new CardTag().fieldId(22L).value("538408")),
                Arguments.of(new CardTag().fieldId(23L).value("1.431E-17")),
                Arguments.of(new CardTag().fieldId(24L).value("32145840.456")),
                Arguments.of(new CardTag().fieldId(25L).value("Новое строковое значение")),
                Arguments.of(new CardTag().fieldId(26L).value("Самый длинный текст...")),
                Arguments.of(new CardTag().fieldId(27L).value("2024-09-25")),
                Arguments.of(new CardTag().fieldId(28L).value("2024-09-25T19:03:57Z")),
                Arguments.of(new CardTag().fieldId(29L).value("false")),
                Arguments.of(new CardTag().fieldId(30L).value("YVhWdVpuRTVOeKh2djCweU0yNdBNVzR6TkNbNU16SXhPRE1nWURndElERmdNREk9")),
                Arguments.of(new CardTag().fieldId(31L).value("d241dee1-1be1-4359-81de-e11be11359e0")),
                Arguments.of(new CardTag().fieldId(32L).value("{\"tg\": {\"a\": 2, \"b\": null}, \"fig\": [false, \"sqrt\"]}")),
                Arguments.of(new CardTag().fieldId(33L).value("2"))
        );
    }


    @Test
    void removeAllTags() throws Exception {
        // region Given
        String url = String.format(CARD_TAG_URL, catalogId, cardId);
        MockHttpServletRequestBuilder requestBuilder = delete(url).header(USER_UUID_HEADER, userID);
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
    @ValueSource(longs = {21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33})
    void removeTag(long fieldId) throws Exception {
        // region Given
        String url = String.format(CARD_TAG_FIELD_ID_URL, catalogId, cardId, fieldId);
        MockHttpServletRequestBuilder requestBuilder = delete(url).header(USER_UUID_HEADER, userID);
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

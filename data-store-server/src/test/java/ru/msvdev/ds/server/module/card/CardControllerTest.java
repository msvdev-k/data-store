package ru.msvdev.ds.server.module.card;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
        value = {"classpath:module/card/card-controller-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
public class CardControllerTest extends ApplicationTest {

    private static final String CARD_URL = "/catalog/%d/card";
    private static final String CARD_ID_URL = "/catalog/%d/card/%d";

    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private String userID;
    private long catalogId;


    @Autowired
    public CardControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() {
        userID = "bfe5e92a-ba1f-4412-a5e9-2aba1fc41272";
        catalogId = 1;
    }


    @Test
    void cardList() throws Exception {
        // region Given
        List<CardResponse> expectedResponse = getDataDBCardResponses().toList();

        String url = String.format(CARD_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, userID);
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

        List<CardResponse> actualResponse = objectMapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedResponse.size(), actualResponse.size());
        for (CardResponse expectedCardResponse : expectedResponse) {
            Optional<CardResponse> optionalCardResponse = actualResponse.stream()
                    .filter(cardResponse -> cardResponse.getId().equals(expectedCardResponse.getId()))
                    .findFirst();

            assertTrue(optionalCardResponse.isPresent());

            List<CardTag> expectedTags = expectedCardResponse.getTags();
            List<CardTag> actualTags = optionalCardResponse.get().getTags();

            assertEquals(expectedTags.size(), actualTags.size());
            for (CardTag expectedTag : expectedTags) {
                assertTrue(actualTags.contains(expectedTag));
            }
        }
        // endregion
    }


    @Test
    void addCard() throws Exception {
        // region Given
        long expectedCardId = 57;
        List<CardTag> cardTags = List.of(
                new CardTag().fieldId(21L).value(""),
                new CardTag().fieldId(22L).value("3259440"),
                new CardTag().fieldId(23L).value("3.14E-19"),
                new CardTag().fieldId(24L).value("59452840.456"),
                new CardTag().fieldId(25L).value("Новое строковое значение"),
                new CardTag().fieldId(26L).value("Самый длинный текст..."),
                new CardTag().fieldId(27L).value("2023-09-25"),
                new CardTag().fieldId(28L).value("2023-09-25T19:03:57Z"),
                new CardTag().fieldId(29L).value("false"),
                new CardTag().fieldId(30L).value("aXVuZnE5NzHvv70yV245MW4zNCA5zzIxODMgYDgtIDFgMDI="),
                new CardTag().fieldId(31L).value("bb5d12b6-3b20-414d-9d10-b63b11714dd2"),
                new CardTag().fieldId(32L).value("{\"tg\": {\"a\": 2, \"b\": null}, \"figure\": [false, \"root\"]}"),
                new CardTag().fieldId(33L).value("2")
        );

        String url = String.format(CARD_URL, catalogId);
        MockHttpServletRequestBuilder requestBuilder = post(url)
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
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        resultActions.andReturn().getResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());

        CardResponse cardResponse = objectMapper.readValue(response.getContentAsString(), CardResponse.class);

        assertNotNull(cardResponse);
        assertEquals(expectedCardId, cardResponse.getId());

        List<CardTag> actualTags = cardResponse.getTags();

        assertEquals(cardTags.size(), actualTags.size());
        for (CardTag expectedTag : cardTags) {
            assertTrue(actualTags.contains(expectedTag));
        }
        // endregion
    }


    @ParameterizedTest
    @MethodSource
    void getCard(CardResponse expectedCardResponse) throws Exception {
        // region Given
        String url = String.format(CARD_ID_URL, catalogId, expectedCardResponse.getId());
        MockHttpServletRequestBuilder requestBuilder = get(url).header(USER_UUID_HEADER, userID);
        // endregion


        // region When
        ResultActions resultActions = mockMvc.perform(requestBuilder);
        // endregion


        // region Then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response = resultActions.andReturn().getResponse();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CardResponse cardResponse = objectMapper.readValue(response.getContentAsString(), CardResponse.class);

        assertNotNull(cardResponse);
        assertEquals(expectedCardResponse.getId(), cardResponse.getId());

        List<CardTag> expectedTags = expectedCardResponse.getTags();
        List<CardTag> actualTags = cardResponse.getTags();

        assertEquals(expectedTags.size(), actualTags.size());
        for (CardTag expectedTag : expectedTags) {
            assertTrue(actualTags.contains(expectedTag));
        }
        // endregion
    }

    private static Stream<Arguments> getCard() {
        return getDataDBCardResponses().map(Arguments::of);
    }


    @ParameterizedTest
    @ValueSource(longs = {41, 42, 43})
    void removeCardById(long cardId) throws Exception {
        // region Given
        String url = String.format(CARD_ID_URL, catalogId, cardId);
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


    private static Stream<CardResponse> getDataDBCardResponses() {
        return Stream.of(
                new CardResponse().id(41L).tags(List.of(
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
                        new CardTag().fieldId(33L).value("1")
                )),
                new CardResponse().id(42L).tags(List.of(
                        new CardTag().fieldId(22L).value("32452840"),
                        new CardTag().fieldId(23L).value("3.14E-9"),
                        new CardTag().fieldId(24L).value("32452840.456"),
                        new CardTag().fieldId(25L).value("Строковое значение"),
                        new CardTag().fieldId(26L).value("Очень длинный текст..."),
                        new CardTag().fieldId(27L).value("2024-06-25"),
                        new CardTag().fieldId(28L).value("2024-06-25T19:03:57Z"),
                        new CardTag().fieldId(29L).value("false")
                )),
                new CardResponse().id(43L).tags(new ArrayList<>())
        );
    }

}

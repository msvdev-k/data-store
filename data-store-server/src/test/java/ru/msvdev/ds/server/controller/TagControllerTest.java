package ru.msvdev.ds.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.msvdev.ds.server.data.entity.Field;
import ru.msvdev.ds.server.openapi.model.*;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class TagControllerTest extends ApplicationTest {

    private static final String BASE_URL = "/catalog";
    private static final String FIELD_URL = "/catalog/%d/field";
    private static final String CARD_URL = "/catalog/%d/card";
    private static final String CARD_ID_URL = "/catalog/%d/card/%d";
    private static final String CARD_TAG_URL = "/catalog/%d/card/%d/tag";
    private static final String CARD_TAG_FIELD_ID_URL = "/catalog/%d/card/%d/tag/%d";


    private static final String USER_UUID_HEADER = "User-UUID";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc;

    private UUID userID;
    private Long catalogId;
    private Map<Long, Field> fields;
    private Long cardId;


    @Autowired
    public TagControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }


    @BeforeEach
    public void beforeEach() throws Exception {
        userID = UUID.randomUUID();
        catalogId = insertCatalog();
        fields = insertFields();
        cardId = insertCard();
    }

    @AfterEach
    public void afterEach() throws Exception {
        deleteCatalog(catalogId);
    }


    @Test
    void crudTest() throws Exception {
        List<CardTag> cardTags = getCardTags();

        // = addTagsRequest ===========================
        CardResponse cardResponse = addTagsRequest(cardTags);

        assertNotNull(cardResponse);
        assertCardTags(cardTags, cardResponse.getTags());


        // = addTagsRequest ===========================
        // Повторное добавление тегов
        CardResponse cardResponse2 = addTagsRequest(cardTags);

        assertNotNull(cardResponse2);
        assertCardTags(cardTags, cardResponse2.getTags());


        // = deleteTagRequest =========================
        CardTag tag = cardTags.get(4);
        cardTags.remove(tag);

        deleteTagRequest(tag.getFieldId());

        CardResponse cardResponse3 = getCardRequest();
        assertNotNull(cardResponse3);
        assertCardTags(cardTags, cardResponse3.getTags());


        // = deleteAllTagsRequest =====================
        deleteAllTagsRequest();

        CardResponse cardResponse4 = getCardRequest();
        assertNotNull(cardResponse4);
        assertTrue(cardResponse4.getTags().isEmpty());
    }


    private CardResponse addTagsRequest(List<CardTag> cardTags) throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(put(String.format(CARD_TAG_URL, catalogId, cardId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardTags))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), CardResponse.class);
    }


    private void deleteTagRequest(long fieldId) throws Exception {
        mockMvc.perform(
                        delete(String.format(CARD_TAG_FIELD_ID_URL, catalogId, cardId, fieldId))
                                .header(USER_UUID_HEADER, userID)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    private void deleteAllTagsRequest() throws Exception {
        mockMvc.perform(
                        delete(String.format(CARD_TAG_URL, catalogId, cardId))
                                .header(USER_UUID_HEADER, userID)
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    private void assertCardTags(List<CardTag> expected, List<CardTag> actual) {
        assertEquals(expected.size(), actual.size());

        for (CardTag actualCardTag : actual) {
            Long fieldId = actualCardTag.getFieldId();
            List<CardTag> equalValues = expected.stream()
                    .filter(cardTag -> Objects.equals(cardTag.getFieldId(), fieldId))
                    .toList();

            assertEquals(1, equalValues.size());

            String expectedValue = equalValues.get(0).getValue();
            String actualValue = actualCardTag.getValue();

            switch (fields.get(fieldId).valueType()) {
                case NULL -> assertTrue(actualValue.isBlank());
                case INTEGER,
                        STRING,
                        TEXT,
                        DATE,
                        BYTES,
                        UUID -> assertEquals(expectedValue, actualValue);
                case DOUBLE -> {
                    double eDouble = Double.parseDouble(expectedValue);
                    double aDouble = Double.parseDouble(actualValue);
                    assertEquals(eDouble, aDouble);
                }
                case BIG_DECIMAL -> {
                    BigDecimal eBigDecimal = new BigDecimal(expectedValue);
                    BigDecimal aBigDecimal = new BigDecimal(actualValue);
                    assertEquals(eBigDecimal, aBigDecimal);
                }
                case DATETIME -> {
                    OffsetDateTime eDateTime = OffsetDateTime.parse(expectedValue);
                    OffsetDateTime aDateTime = OffsetDateTime.parse(actualValue);
                    assertEquals(eDateTime.toEpochSecond(), aDateTime.toEpochSecond());
                }
                case BOOLEAN -> {
                    boolean eBoolean = Boolean.parseBoolean(expectedValue);
                    boolean aBoolean = Boolean.parseBoolean(actualValue);
                    assertEquals(eBoolean, aBoolean);
                }
                case JSON -> {
                    try {
                        Field eField = objectMapper.readValue(expectedValue, Field.class);
                        Field aField = objectMapper.readValue(actualValue, Field.class);
                        assertEquals(eField, aField);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        }
    }


    private List<CardTag> getCardTags() {
        return fields.values().stream()
                .map(field -> {
                    CardTag cardTag = new CardTag();
                    cardTag.setFieldId(field.id());

                    switch (field.valueType()) {
                        case NULL -> cardTag.setValue(null);
                        case INTEGER -> cardTag.setValue("45698575");
                        case DOUBLE -> cardTag.setValue("125.367e54");
                        case BIG_DECIMAL -> cardTag.setValue("3.14314");
                        case STRING -> cardTag.setValue("Строка 2");
                        case TEXT -> cardTag.setValue("Предложение, состоящее из пяти коротких слов.");
                        case DATE -> cardTag.setValue(LocalDate.now().toString());
                        case DATETIME -> cardTag.setValue(OffsetDateTime.now().toString());
                        case BOOLEAN -> cardTag.setValue("true");
                        case BYTES -> cardTag.setValue("Y3NiNmY0N2QtNjM0Ni00YzEzLWI2ZjQtN2Q2MzQ2MWMxMw==");
                        case UUID -> cardTag.setValue(UUID.randomUUID().toString());
                        case JSON -> {
                            try {
                                cardTag.setValue(objectMapper.writeValueAsString(field));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    return cardTag;
                }).collect(Collectors.toList());
    }


    private Long insertCard() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(post(String.format(CARD_URL, catalogId))
                        .header(USER_UUID_HEADER, userID)
                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                        .content("[]")
                )
//                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CardResponse cardResponse = objectMapper.readValue(response.getContentAsString(), CardResponse.class);
        assertNotNull(cardResponse.getId());

        return cardResponse.getId();
    }


    private CardResponse getCardRequest() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get(String.format(CARD_ID_URL, catalogId, cardId)).header(USER_UUID_HEADER, userID))
//                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        return objectMapper.readValue(response.getContentAsString(), CardResponse.class);
    }


    private Map<Long, Field> insertFields() {
        Map<Long, Field> fieldMap = new HashMap<>();
        getFieldRequests().stream()
                .map(fieldRequest -> {
                    try {
                        MockHttpServletResponse response = mockMvc
                                .perform(post(String.format(FIELD_URL, catalogId))
                                        .header(USER_UUID_HEADER, userID)
                                        .header(CONTENT_TYPE_HEADER, MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(fieldRequest))
                                )
//                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andReturn().getResponse();

                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        FieldResponse fieldResponse = objectMapper.readValue(response.getContentAsString(), FieldResponse.class);

                        assertNotNull(fieldResponse.getId());
                        assertNotNull(fieldResponse.getType());

                        return fieldResponse;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(fieldResponse -> new Field(
                        fieldResponse.getId(),
                        catalogId,
                        fieldResponse.getOrder(),
                        fieldResponse.getName(),
                        fieldResponse.getDescription(),
                        ValueType.valueOf(fieldResponse.getType().name()),
                        fieldResponse.getFormat()
                )).forEach(field -> fieldMap.put(field.id(), field));

        return fieldMap;
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


    private Long insertCatalog() throws Exception {
        CatalogRequest catalogRequest = new CatalogRequest("Каталог");
        MockHttpServletResponse response = mockMvc
                .perform(post(BASE_URL)
                        .header(USER_UUID_HEADER, userID)
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
        mockMvc.perform(delete(BASE_URL + "/" + catalogId).header(USER_UUID_HEADER, userID))
//                .andDo(print())
                .andExpect(status().isNoContent());
    }

}

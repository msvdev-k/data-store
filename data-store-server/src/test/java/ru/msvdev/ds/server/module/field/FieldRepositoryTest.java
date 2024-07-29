package ru.msvdev.ds.server.module.field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.field.entity.Field;
import ru.msvdev.ds.server.module.field.repository.FieldRepository;
import ru.msvdev.ds.server.module.value.base.ValueType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/field/field-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FieldRepositoryTest extends ApplicationTest {

    private final FieldRepository fieldRepository;

    @Autowired
    public FieldRepositoryTest(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    private long catalogId;

    @BeforeEach
    void setUp() {
        catalogId = 1;
    }


    @ParameterizedTest
    @ValueSource(longs = {0, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23})
    void existsById(long id) {
        // region Given
        // endregion


        // region When
        boolean existFlag = fieldRepository.existsById(catalogId, id);
        // endregion


        // region Then
        assertTrue(existFlag || id == 0);
        assertFalse(id == 0 && existFlag);
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "10, none",
            "11, null",
            "12, integer",
            "13, double",
            "14, big_decimal",
            "15, string",
            "16, text",
            "17, date",
            "18, datetime",
            "19, boolean",
            "20, bytes",
            "21, uuid",
            "22, json",
            "23, file_id"
    })
    void findIdByName(Long id, String name) {
        // region Given
        if (id == 10) id = null;
        // endregion

        // region When
        Long foundId = fieldRepository.findIdByName(catalogId, name);
        // endregion

        // region Then
        assertEquals(id, foundId);
        // endregion
    }


    @Test
    void findById() {
        // region Given
        long fieldId = 12;
        Field field = new Field(fieldId, 1, 2, "integer", "Long", ValueType.INTEGER, null);
        // endregion


        // region When
        Field foundField = fieldRepository.findById(catalogId, fieldId);
        // endregion


        // region Then
        assertEquals(field, foundField);
        // endregion
    }


    @Test
    void findAll() {
        // region Given
        Field[] fields = {
                new Field(11, 1, 1, "null", null, ValueType.NULL, null),
                new Field(12, 1, 2, "integer", "Long", ValueType.INTEGER, null),
                new Field(13, 1, 3, "double", "Double", ValueType.DOUBLE, null),
                new Field(14, 1, 4, "big_decimal", "BigDecimal", ValueType.BIG_DECIMAL, null),
                new Field(15, 1, 5, "string", "String", ValueType.STRING, null),
                new Field(16, 1, 6, "text", "String", ValueType.TEXT, null),
                new Field(17, 1, 7, "date", "LocalDate", ValueType.DATE, null),
                new Field(18, 1, 8, "datetime", "OffsetDateTime", ValueType.DATETIME, null),
                new Field(19, 1, 9, "boolean", "Boolean", ValueType.BOOLEAN, null),
                new Field(20, 1, 10, "bytes", "String", ValueType.BYTES, null),
                new Field(21, 1, 11, "uuid", "UUID", ValueType.UUID, null),
                new Field(22, 1, 12, "json", "String", ValueType.JSON, null),
                new Field(23, 1, 13, "file_id", null, ValueType.FILE_ID, null)
        };

        // endregion


        // region When
        List<Field> foundFields = fieldRepository.findAll(catalogId);
        // endregion


        // region Then
        assertEquals(fields.length, foundFields.size());

        for (Field field : fields) {
            assertTrue(foundFields.contains(field));
        }
        // endregion
    }


    @Test
    void insert() {
        // region Given
        long id = 37;
        int order = 69;
        String name = "Новое поле";
        String format = "MyFormat";
        String description = "DESCRIPTION";
        // endregion


        // region When
        Field insertedField = fieldRepository.insert(catalogId, order, name, ValueType.STRING, format, description);
        // endregion


        // region Then
        assertNotNull(insertedField);
        assertEquals(id, insertedField.id());
        assertEquals(catalogId, insertedField.catalogId());
        assertEquals(order, insertedField.order());
        assertEquals(name, insertedField.name());
        assertEquals(description, insertedField.description());
        assertEquals(ValueType.STRING, insertedField.valueType());
        assertEquals(format, insertedField.format());
        // endregion
    }


    @Test
    void updateOrder() {
        // region Given
        long id = 12;
        int newOrder = 121;
        // endregion


        // region When
        boolean updateFlag = fieldRepository.updateOrder(catalogId, id, newOrder);
        // endregion


        // region Then
        assertTrue(updateFlag);

        Field upadtedField = fieldRepository.findById(catalogId, id);
        assertNotNull(upadtedField);
        assertEquals(newOrder, upadtedField.order());
        // endregion
    }


    @Test
    void updateName() {
        // region Given
        long id = 12;
        String newName = "Новое имя поля";
        // endregion


        // region When
        boolean updateFlag = fieldRepository.updateName(catalogId, id, newName);
        // endregion


        // region Then
        assertTrue(updateFlag);

        Field upadtedField = fieldRepository.findById(catalogId, id);
        assertNotNull(upadtedField);
        assertEquals(newName, upadtedField.name());
        // endregion
    }


    @Test
    void updateDescription() {
        // region Given
        long id = 12;
        String newDescription = "Новое описание поля";
        // endregion


        // region When
        boolean updateFlag = fieldRepository.updateDescription(catalogId, id, newDescription);
        // endregion


        // region Then
        assertTrue(updateFlag);

        Field upadtedField = fieldRepository.findById(catalogId, id);
        assertNotNull(upadtedField);
        assertEquals(newDescription, upadtedField.description());
        // endregion
    }


    @Test
    void deleteById() {
        // region Given
        int id = 12;
        // endregion


        // region When
        boolean deleteFlag = fieldRepository.deleteById(catalogId, id);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        assertFalse(fieldRepository.existsById(catalogId, id));
        // endregion
    }


    @ParameterizedTest
    @EnumSource(ValueType.class)
    void insertAllTypesTest(ValueType fieldType) {
        // region Given
        long id = 37;
        int order = 69;
        // endregion


        // region When
        Field insertedField = fieldRepository.insert(catalogId, order, fieldType.name(), fieldType, null, null);
        // endregion


        // region Then
        assertNotNull(insertedField);
        assertEquals(id, insertedField.id());
        assertEquals(catalogId, insertedField.catalogId());
        assertEquals(order, insertedField.order());
        assertEquals(fieldType.name(), insertedField.name());
        assertNull(insertedField.description());
        assertEquals(fieldType, insertedField.valueType());
        assertNull(insertedField.format());
        // endregion
    }

}
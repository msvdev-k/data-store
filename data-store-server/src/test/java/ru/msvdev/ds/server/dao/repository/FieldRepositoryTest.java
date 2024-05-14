package ru.msvdev.ds.server.dao.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.Catalog;
import ru.msvdev.ds.server.dao.entity.Field;
import ru.msvdev.ds.server.dao.entity.FieldType;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FieldRepositoryTest extends ApplicationTest {

    private final CatalogRepository catalogRepository;
    private final FieldRepository fieldRepository;
    private final FieldTypeRepository fieldTypeRepository;

    @Autowired
    public FieldRepositoryTest(CatalogRepository catalogRepository, FieldRepository fieldRepository, FieldTypeRepository fieldTypeRepository) {
        this.catalogRepository = catalogRepository;
        this.fieldRepository = fieldRepository;
        this.fieldTypeRepository = fieldTypeRepository;
    }

    private Catalog catalog;

    @BeforeEach
    void setUp() {
        catalog = catalogRepository.insert("Каталог", null);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {
        int order1 = 1;
        String name1 = "первое поле";
        ValueType type1 = ValueType.NULL;

        int order2 = 2;
        String name2 = "второе поле";
        ValueType type2 = ValueType.INTEGER;
        String format2 = "Формат поля";
        String description2 = "Описание поля";


        // = insert =====================

        Field insertedField1 = fieldRepository.insert(catalog.id(),
                order1, name1, type1, null, null);

        Field insertedField2 = fieldRepository.insert(catalog.id(),
                order2, name2, type2, format2, description2);

        assertTrue(insertedField1.id() > 0);
        assertEquals(catalog.id(), insertedField1.catalogId());
        assertEquals(order1, insertedField1.order());
        assertEquals(name1, insertedField1.name());
        assertEquals(type1, insertedField1.valueType());
        assertNull(insertedField1.format());
        assertNull(insertedField1.description());

        assertTrue(insertedField2.id() > 0);
        assertEquals(catalog.id(), insertedField2.catalogId());
        assertEquals(order2, insertedField2.order());
        assertEquals(name2, insertedField2.name());
        assertEquals(type2, insertedField2.valueType());
        assertEquals(format2, insertedField2.format());
        assertEquals(description2, insertedField2.description());

        // = findIdByName ===============

        Long fieldTemplateId1 = fieldRepository.findIdByName(catalog.id(), insertedField1.name());
        Long fieldTemplateId2 = fieldRepository.findIdByName(catalog.id(), insertedField2.name());
        Long fieldTemplateId3 = fieldRepository.findIdByName(catalog.id(), "fieldTemplateId3");

        assertEquals(insertedField1.id(), fieldTemplateId1);
        assertEquals(insertedField2.id(), fieldTemplateId2);
        assertNull(fieldTemplateId3);

        // = findById ===================

        Optional<Field> fieldTemplateOptional1 = fieldRepository.findById(catalog.id(), insertedField1.id());
        Optional<Field> fieldTemplateOptional2 = fieldRepository.findById(catalog.id(), insertedField2.id());

        assertTrue(fieldTemplateOptional1.isPresent());
        assertTrue(fieldTemplateOptional2.isPresent());
        assertEquals(insertedField1, fieldTemplateOptional1.get());
        assertEquals(insertedField2, fieldTemplateOptional2.get());

        // = findAll ====================

        List<Field> fieldList = fieldRepository.findAll(catalog.id());

        assertEquals(2, fieldList.size());
        assertTrue(fieldList.contains(insertedField1));
        assertTrue(fieldList.contains(insertedField2));

        // = updateOrder ================

        int newOrder = 3;
        assertTrue(fieldRepository.updateOrder(catalog.id(), fieldTemplateId1, newOrder));

        Optional<Field> optional = fieldRepository.findById(catalog.id(), fieldTemplateId1);
        assertTrue(optional.isPresent());
        assertEquals(newOrder, optional.get().order());

        // = updateName =================

        String newName = "Новое название поля";
        assertTrue(fieldRepository.updateName(catalog.id(), fieldTemplateId1, newName));

        optional = fieldRepository.findById(catalog.id(), fieldTemplateId1);
        assertTrue(optional.isPresent());
        assertEquals(newName, optional.get().name());

        // = updateDescription ==========

        String newDescription = "Новое описание поля";
        assertTrue(fieldRepository.updateDescription(catalog.id(), fieldTemplateId1, newDescription));

        optional = fieldRepository.findById(catalog.id(), fieldTemplateId1);
        assertTrue(optional.isPresent());
        assertEquals(newDescription, optional.get().description());

        // = deleteById =================

        assertTrue(fieldRepository.deleteById(catalog.id(), fieldTemplateId1));
        assertTrue(fieldRepository.deleteById(catalog.id(), fieldTemplateId2));

        fieldTemplateOptional1 = fieldRepository.findById(catalog.id(), fieldTemplateId1);
        fieldTemplateOptional2 = fieldRepository.findById(catalog.id(), fieldTemplateId2);

        assertTrue(fieldTemplateOptional1.isEmpty());
        assertTrue(fieldTemplateOptional2.isEmpty());

        fieldList = fieldRepository.findAll(catalog.id());

        assertTrue(fieldList.isEmpty());
    }


    @Test
    void existByIdTest() {
        Field field = fieldRepository.insert(catalog.id(), 1, "name", ValueType.NULL, null, null);

        assertTrue(fieldRepository.existsById(catalog.id(), field.id()));
        assertFalse(fieldRepository.existsById(catalog.id(), 0));
    }


    @Test
    void insertAllTypesTest() {
        List<FieldType> fieldTypes = fieldTypeRepository.findAll();

        int order = 1;

        for (FieldType fieldType : fieldTypes) {

            Field field = fieldRepository.insert(catalog.id(),
                    order, fieldType.type().name(), fieldType.type(), null, null);

            assertTrue(field.id() > 0);
            assertEquals(catalog.id(), field.catalogId());
            assertEquals(order++, field.order());
            assertEquals(fieldType.type().name(), field.name());
            assertEquals(fieldType.type(), field.valueType());
            assertNull(field.format());
            assertNull(field.description());

            System.out.println(field);
        }
    }

}
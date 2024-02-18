package ru.msvdev.ds.server.data.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.data.entity.Card;
import ru.msvdev.ds.server.data.entity.Catalog;
import ru.msvdev.ds.server.data.entity.Field;
import ru.msvdev.ds.server.data.entity.Tag;
import ru.msvdev.ds.server.utils.type.ConstantValue;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest extends ApplicationTest {

    private final CatalogRepository catalogRepository;
    private final FieldRepository fieldRepository;
    private final CardRepository cardRepository;
    private final TagRepository tagRepository;

    @Autowired
    TagRepositoryTest(CatalogRepository catalogRepository, FieldRepository fieldRepository, CardRepository cardRepository, TagRepository tagRepository) {
        this.catalogRepository = catalogRepository;
        this.fieldRepository = fieldRepository;
        this.cardRepository = cardRepository;
        this.tagRepository = tagRepository;
    }


    private Catalog catalog;
    private Card card;
    private Field fieldNull;
    private Field fieldBoolean;

    @BeforeEach
    void setUp() {
        catalog = catalogRepository.insert("Каталог", null);
        card = cardRepository.insert(catalog.id());
        fieldNull = fieldRepository.insert(catalog.id(), 1, "fieldNull", ValueType.NULL, null, null);
        fieldBoolean = fieldRepository.insert(catalog.id(), 2, "fieldBoolean", ValueType.BOOLEAN, null, null);
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void crudTest() {

        // = insert =====================
        assertTrue(tagRepository.insert(card.id(), fieldNull.id(), ConstantValue.NULL.id));
        assertTrue(tagRepository.insert(card.id(), fieldBoolean.id(), ConstantValue.TRUE.id));


        // = isExists ===================
        assertTrue(tagRepository.isExists(card.id(), fieldNull.id(), ConstantValue.NULL.id));
        assertTrue(tagRepository.isExists(card.id(), fieldBoolean.id(), ConstantValue.TRUE.id));
        assertFalse(tagRepository.isExists(card.id(), fieldBoolean.id(), ConstantValue.FALSE.id));


        // = findAll ====================
        List<Tag> tags = tagRepository.findAll(catalog.id(), card.id());

        assertFalse(tags.isEmpty());
        assertEquals(2, tags.size());

        for (Tag tag : tags) {
            assertEquals(card.id(), tag.cardId());
        }

        List<Long> fieldIdList = tags.stream().map(Tag::fieldId).toList();
        List<ValueType> valueTypeList = tags.stream().map(Tag::valueType).toList();
        List<Long> valueIdList = tags.stream().map(Tag::valueId).toList();

        assertTrue(fieldIdList.contains(fieldNull.id()));
        assertTrue(fieldIdList.contains(fieldBoolean.id()));

        assertTrue(valueTypeList.contains(ValueType.NULL));
        assertTrue(valueTypeList.contains(ValueType.BOOLEAN));

        assertTrue(valueIdList.contains(ConstantValue.NULL.id));
        assertTrue(valueIdList.contains(ConstantValue.TRUE.id));


        // = delete =====================
        assertTrue(tagRepository.delete(catalog.id(), card.id()));

        tags = tagRepository.findAll(catalog.id(), card.id());
        assertTrue(tags.isEmpty());

    }


    @Test
    void deleteTagTest() {
        // = insert =====================
        assertTrue(tagRepository.insert(card.id(), fieldNull.id(), ConstantValue.NULL.id));
        assertTrue(tagRepository.insert(card.id(), fieldBoolean.id(), ConstantValue.TRUE.id));


        // = delete =====================
        assertTrue(tagRepository.delete(catalog.id(), card.id(), fieldNull.id()));

        List<Tag> tags = tagRepository.findAll(catalog.id(), card.id());
        assertFalse(tags.isEmpty());
        assertEquals(1, tags.size());

        Tag tag = tags.get(0);
        assertEquals(card.id(), tag.cardId());
        assertEquals(fieldBoolean.id(), tag.fieldId());
        assertEquals(ValueType.BOOLEAN, tag.valueType());
        assertEquals(ConstantValue.TRUE.id, tag.valueId());
    }

}
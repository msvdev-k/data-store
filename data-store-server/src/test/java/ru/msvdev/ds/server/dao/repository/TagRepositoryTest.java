package ru.msvdev.ds.server.dao.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.dao.entity.Tag;
import ru.msvdev.ds.server.utils.type.ValueType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql({"classpath:db/repository/tag-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest extends ApplicationTest {

    private final TagRepository tagRepository;

    @Autowired
    TagRepositoryTest(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @ParameterizedTest
    @CsvSource({
            "41, 21, 51, true",
            "41, 22, 52, true",
            "41, 23, 53, true",
            "41, 24, 54, true",
            "41, 25, 55, true",
            "41, 26, 56, true",
            "41, 27, 57, true",
            "41, 28, 58, true",
            "41, 29, 59, true",
            "41, 30, 60, true",
            "41, 31, 61, true",
            "41, 32, 62, true",
            "41, 32, 67, false"
    })
    void exists(long cardId, long fieldId, long valueId, boolean expectedResult) {
        // region Given
        // endregion

        // region When
        boolean actualResult = tagRepository.exists(cardId, fieldId, valueId);
        // endregion

        // region Then
        assertEquals(expectedResult, actualResult);
        // endregion
    }


    @Test
    void findAll() {
        // region Given
        long catalogID = 1;
        long cardId = 41;

        Tag[] tags = new Tag[]{
                new Tag(41, 21, ValueType.NULL, 51),
                new Tag(41, 22, ValueType.INTEGER, 52),
                new Tag(41, 23, ValueType.DOUBLE, 53),
                new Tag(41, 24, ValueType.BIG_DECIMAL, 54),
                new Tag(41, 25, ValueType.STRING, 55),
                new Tag(41, 26, ValueType.TEXT, 56),
                new Tag(41, 27, ValueType.DATE, 57),
                new Tag(41, 28, ValueType.DATETIME, 58),
                new Tag(41, 29, ValueType.BOOLEAN, 59),
                new Tag(41, 30, ValueType.BYTES, 60),
                new Tag(41, 31, ValueType.UUID, 61),
                new Tag(41, 32, ValueType.JSON, 62),
                new Tag(41, 33, ValueType.FILE_ID, 63)
        };
        // endregion

        // region When
        List<Tag> foundTags = tagRepository.findAll(catalogID, cardId);
        // endregion

        // region Then
        assertEquals(tags.length, foundTags.size());
        for (Tag t : tags) {
            assertTrue(foundTags.contains(t));
        }
        // endregion
    }

    @Test
    void insert() {
        // region Given
        long cardId = 41;
        long fieldId = 22;
        long valueId = 67;
        // endregion

        // region When
        boolean insertedFlag = tagRepository.insert(cardId, fieldId, valueId);
        // endregion

        // region Then
        assertTrue(insertedFlag);
        assertTrue(tagRepository.exists(cardId, fieldId, valueId));
        // endregion
    }


    @ParameterizedTest
    @CsvSource({
            "41, 21, 51",
            "41, 22, 52",
            "41, 23, 53",
            "41, 24, 54",
            "41, 25, 55",
            "41, 26, 56",
            "41, 27, 57",
            "41, 28, 58",
            "41, 29, 59",
            "41, 30, 60",
            "41, 31, 61",
            "41, 32, 62",
            "41, 32, 67"
    })
    void delete(long cardId, long fieldId, long valueId) {
        // region Given
        long catalogId = 1;
        // endregion

        // region When
        boolean deleteFlag = tagRepository.delete(catalogId, cardId, fieldId);
        // endregion

        // region Then
        assertTrue(deleteFlag);
        assertFalse(tagRepository.exists(cardId, fieldId, valueId));
        // endregion
    }


    @Test
    void deleteAll() {
        // region Given
        long catalogId = 1;
        long cardId = 41;

        Tag[] tags = new Tag[]{
                new Tag(41, 21, ValueType.NULL, 51),
                new Tag(41, 22, ValueType.INTEGER, 52),
                new Tag(41, 23, ValueType.DOUBLE, 53),
                new Tag(41, 24, ValueType.BIG_DECIMAL, 54),
                new Tag(41, 25, ValueType.STRING, 55),
                new Tag(41, 26, ValueType.TEXT, 56),
                new Tag(41, 27, ValueType.DATE, 57),
                new Tag(41, 28, ValueType.DATETIME, 58),
                new Tag(41, 29, ValueType.BOOLEAN, 59),
                new Tag(41, 30, ValueType.BYTES, 60),
                new Tag(41, 31, ValueType.UUID, 61),
                new Tag(41, 32, ValueType.JSON, 62),
                new Tag(41, 33, ValueType.FILE_ID, 63)
        };
        // endregion

        // region When
        boolean deleteFlag = tagRepository.delete(catalogId, cardId);
        // endregion

        // region Then
        assertTrue(true);
        for (Tag t : tags) {
            assertFalse(tagRepository.exists(t.cardId(), t.fieldId(), t.valueId()));
        }
        // endregion
    }
}
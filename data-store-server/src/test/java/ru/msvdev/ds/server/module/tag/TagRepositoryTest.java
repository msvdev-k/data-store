package ru.msvdev.ds.server.module.tag;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.tag.entity.Tag;
import ru.msvdev.ds.server.module.tag.repository.TagRepository;
import ru.msvdev.ds.server.module.value.base.DataType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/tag/tag-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest extends ApplicationTest {

    private final TagRepository tagRepository;

    @Autowired
    TagRepositoryTest(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @ParameterizedTest
    @CsvSource({
            "41, 21,  -1, true",
            "41, 22, 152, true",
            "41, 23, 153, true",
            "41, 24, 154, true",
            "41, 25, 155, true",
            "41, 26, 156, true",
            "41, 27, 157, true",
            "41, 28, 158, true",
            "41, 29,   0, true",
            "41, 30, 160, true",
            "41, 31, 161, true",
            "41, 32, 162, true",
            "41, 32, 167, false"
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
                new Tag(41, 21, DataType.NULL, -1),
                new Tag(41, 22, DataType.INTEGER, 152),
                new Tag(41, 23, DataType.DOUBLE, 153),
                new Tag(41, 24, DataType.BIG_DECIMAL, 154),
                new Tag(41, 25, DataType.STRING, 155),
                new Tag(41, 26, DataType.TEXT, 156),
                new Tag(41, 27, DataType.DATE, 157),
                new Tag(41, 28, DataType.DATETIME, 158),
                new Tag(41, 29, DataType.BOOLEAN, 0),
                new Tag(41, 30, DataType.BYTES, 160),
                new Tag(41, 31, DataType.UUID, 161),
                new Tag(41, 32, DataType.JSON, 162),
                new Tag(41, 33, DataType.FILE_ID, 163)
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
        long valueId = 167;
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
            "41, 21,  -1",
            "41, 22, 152",
            "41, 23, 153",
            "41, 24, 154",
            "41, 25, 155",
            "41, 26, 156",
            "41, 27, 157",
            "41, 28, 158",
            "41, 29,   0",
            "41, 30, 160",
            "41, 31, 161",
            "41, 32, 162",
            "41, 33, 163"
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
                new Tag(41, 21, DataType.NULL, -1),
                new Tag(41, 22, DataType.INTEGER, 152),
                new Tag(41, 23, DataType.DOUBLE, 153),
                new Tag(41, 24, DataType.BIG_DECIMAL, 154),
                new Tag(41, 25, DataType.STRING, 155),
                new Tag(41, 26, DataType.TEXT, 156),
                new Tag(41, 27, DataType.DATE, 157),
                new Tag(41, 28, DataType.DATETIME, 158),
                new Tag(41, 29, DataType.BOOLEAN, 0),
                new Tag(41, 30, DataType.BYTES, 160),
                new Tag(41, 31, DataType.UUID, 161),
                new Tag(41, 32, DataType.JSON, 162),
                new Tag(41, 33, DataType.FILE_ID, 163)
        };
        // endregion


        // region When
        boolean deleteFlag = tagRepository.delete(catalogId, cardId);
        // endregion


        // region Then
        assertTrue(deleteFlag);
        for (Tag t : tags) {
            assertFalse(tagRepository.exists(t.cardId(), t.fieldId(), t.valueId()));
        }
        // endregion
    }
}
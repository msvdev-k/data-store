package ru.msvdev.ds.server.module.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.msvdev.ds.server.base.ApplicationTest;
import ru.msvdev.ds.server.module.value.repository.DateTimeValueRepository;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;


@DataJdbcTest
@Sql(
        value = {"classpath:module/value/value-repository-test.sql"},
        config = @SqlConfig(encoding = "UTF8")
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DateTimeValueRepositoryTest extends ApplicationTest {

    private final DateTimeValueRepository valueRepository;

    @Autowired
    public DateTimeValueRepositoryTest(DateTimeValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }


    @Test
    void findIdByValue() {
        // region Given
        long id = 127;
        OffsetDateTime value = OffsetDateTime.parse("2024-06-25T19:03:57.045Z");
        // endregion

        // region When
        Long idByValue = valueRepository.findIdByValue(value);
        // endregion

        // region Then
        assertNotNull(idByValue);
        assertEquals(id, idByValue);
        //endregion
    }


    @Test
    void findValueById() {
        // region Given
        long id = 127;
        OffsetDateTime value = OffsetDateTime.parse("2024-06-25T19:03:57.045Z");
        // endregion

        // region When
        OffsetDateTime valueById = valueRepository.findValueById(id);
        // endregion

        // region Then
        assertNotNull(valueById);
        assertEquals(value, valueById);
        //endregion
    }


    @Test
    void insert() {
        // region Given
        long id = 137;
        OffsetDateTime value = OffsetDateTime.now();
        // endregion

        // region When
        Long insertedId = valueRepository.insert(value);
        // endregion

        // region Then
        assertEquals(id, insertedId);

        OffsetDateTime valueById = valueRepository.findValueById(insertedId);
        assertNotNull(valueById);
        assertEquals(value.toEpochSecond(), valueById.toEpochSecond());
        //endregion
    }
}

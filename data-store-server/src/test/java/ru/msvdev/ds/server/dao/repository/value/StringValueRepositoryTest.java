package ru.msvdev.ds.server.dao.repository.value;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import ru.msvdev.ds.server.base.ApplicationTest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJdbcTest
@Sql({"classpath:db/repository/value-repository-test.sql"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StringValueRepositoryTest extends ApplicationTest {

    private final StringValueRepository valueRepository;

    @Autowired
    public StringValueRepositoryTest(StringValueRepository valueRepository) {
        this.valueRepository = valueRepository;
    }


    @Test
    void findIdByValue() {
        // region Given
        long id = 24;
        String value = "Строковое значение";
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
        long id = 24;
        String value = "Строковое значение";
        // endregion

        // region When
        String valueById = valueRepository.findValueById(id);
        // endregion

        // region Then
        assertNotNull(valueById);
        assertEquals(value, valueById);
        //endregion
    }


    @Test
    void insert() {
        // region Given
        long id = 37;
        String value = "Строка сообщения. " + OffsetDateTime.now();
        // endregion

        // region When
        Long insertedId = valueRepository.insert(value);
        // endregion

        // region Then
        assertEquals(id, insertedId);

        String valueById = valueRepository.findValueById(insertedId);
        assertNotNull(valueById);
        assertEquals(value, valueById);
        //endregion
    }
}